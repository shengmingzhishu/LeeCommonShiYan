#!/bin/bash

# 灵力检测实验室系统完整部署脚本
# 作者: lingli
# 日期: 2023-11-28
# 功能: 完整系统的自动部署和启动

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 打印函数
print_header() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}  灵力检测实验室系统完整部署脚本${NC}"
    echo -e "${BLUE}============================================${NC}"
    echo ""
}

print_step() {
    echo -e "${CYAN}[步骤 $1]${NC} $2"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${PURPLE}ℹ $1${NC}"
}

# 显示系统信息
show_system_info() {
    print_info "系统信息:"
    echo "  项目名称: 灵力检测实验室系统"
    echo "  开发时间: 2023-11-28"
    echo "  开发者: lingli"
    echo "  系统类型: 微服务架构"
    echo "  技术栈: Java + Spring Boot + UniApp"
    echo ""
}

# 检查环境依赖
check_dependencies() {
    print_step "1" "检查环境依赖"
    
    # 检查Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1)
        print_success "Java已安装: $JAVA_VERSION"
    else
        print_error "Java未安装，请先安装Java 11+"
        echo "  Ubuntu/Debian: sudo apt install openjdk-11-jdk"
        echo "  CentOS/RHEL: sudo yum install java-11-openjdk-devel"
        echo "  macOS: brew install openjdk@11"
        return 1
    fi
    
    # 检查Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n1)
        print_success "Maven已安装: $MVN_VERSION"
    else
        print_error "Maven未安装，请先安装Maven 3.6+"
        return 1
    fi
    
    # 检查Git
    if command -v git &> /dev/null; then
        GIT_VERSION=$(git --version)
        print_success "Git已安装: $GIT_VERSION"
    else
        print_warning "Git未安装，部分功能可能受限"
    fi
    
    echo ""
    return 0
}

# 启动基础服务
start_basic_services() {
    print_step "2" "启动基础服务"
    
    # 启动MySQL
    print_info "检查MySQL服务..."
    if command -v systemctl &> /dev/null; then
        sudo systemctl start mysql 2>/dev/null || sudo systemctl start mysqld 2>/dev/null
        if systemctl is-active --quiet mysql || systemctl is-active --quiet mysqld; then
            print_success "MySQL服务启动成功"
        else
            print_warning "MySQL服务启动失败，请手动启动MySQL"
        fi
    else
        print_warning "无法管理MySQL服务，请确保MySQL正在运行"
    fi
    
    # 启动Redis
    print_info "检查Redis服务..."
    if command -v systemctl &> /dev/null; then
        sudo systemctl start redis 2>/dev/null || sudo systemctl start redis-server 2>/dev/null
        if systemctl is-active --quiet redis || systemctl is-active --quiet redis-server; then
            print_success "Redis服务启动成功"
        else
            print_warning "Redis服务启动失败，请手动启动Redis"
        fi
    else
        print_warning "无法管理Redis服务，请确保Redis正在运行"
    fi
    
    echo ""
}

# 初始化数据库
init_database() {
    print_step "3" "初始化数据库"
    
    if [ -f "./database/init_db.sh" ]; then
        print_info "执行数据库初始化脚本..."
        if chmod +x ./database/init_db.sh; then
            ./database/init_db.sh dev
            print_success "数据库初始化完成"
        else
            print_error "无法执行数据库初始化脚本"
            return 1
        fi
    else
        print_warning "数据库初始化脚本不存在，跳过数据库初始化"
    fi
    
    # 执行数据库扩展脚本
    if [ -f "./database/migration/01_extend_geography_business.sql" ]; then
        print_info "执行数据库扩展脚本..."
        mysql -hlocalhost -P3306 -uroot -proot lingli_dev < ./database/migration/01_extend_geography_business.sql
        print_success "数据库扩展完成"
    fi
    
    echo ""
}

# 编译后端项目
build_backend() {
    print_step "4" "编译后端项目"
    
    if [ -f "backend/pom.xml" ]; then
        cd backend
        
        print_info "编译公共模块..."
        mvn clean install -DskipTests -pl common -q
        print_success "公共模块编译完成"
        
        print_info "编译用户服务..."
        mvn clean install -DskipTests -pl user-service -q
        print_success "用户服务编译完成"
        
        print_info "编译商品服务..."
        mvn clean install -DskipTests -pl product-service -q
        print_success "商品服务编译完成"
        
        print_info "编译地理位置服务..."
        mvn clean install -DskipTests -pl location-service -q
        print_success "地理位置服务编译完成"
        
        print_info "编译订单服务..."
        mvn clean install -DskipTests -pl order-service -q
        print_success "订单服务编译完成"
        
        cd ..
        print_success "后端项目编译完成"
    else
        print_error "后端项目目录不存在"
        return 1
    fi
    
    echo ""
}

# 启动后端服务
start_backend_services() {
    print_step "5" "启动后端服务"
    
    # 创建日志目录
    mkdir -p logs
    
    # 启动用户服务
    if [ -f "backend/user-service/target/user-service-1.0.0.jar" ]; then
        print_info "启动用户服务 (端口: 8081)..."
        cd backend/user-service
        nohup java -jar target/user-service-1.0.0.jar > ../../logs/user-service.log 2>&1 &
        USER_SERVICE_PID=$!
        echo $USER_SERVICE_PID > ../../logs/user-service.pid
        print_success "用户服务启动成功 (PID: $USER_SERVICE_PID)"
        cd ../..
    else
        print_error "用户服务jar包不存在"
    fi
    
    # 启动商品服务
    if [ -f "backend/product-service/target/product-service-1.0.0.jar" ]; then
        print_info "启动商品服务 (端口: 8082)..."
        cd backend/product-service
        nohup java -jar target/product-service-1.0.0.jar > ../../logs/product-service.log 2>&1 &
        PRODUCT_SERVICE_PID=$!
        echo $PRODUCT_SERVICE_PID > ../../logs/product-service.pid
        print_success "商品服务启动成功 (PID: $PRODUCT_SERVICE_PID)"
        cd ../..
    else
        print_error "商品服务jar包不存在"
    fi
    
    # 启动地理位置服务
    if [ -f "backend/location-service/target/location-service-1.0.0.jar" ]; then
        print_info "启动地理位置服务 (端口: 8083)..."
        cd backend/location-service
        nohup java -jar target/location-service-1.0.0.jar > ../../logs/location-service.log 2>&1 &
        LOCATION_SERVICE_PID=$!
        echo $LOCATION_SERVICE_PID > ../../logs/location-service.pid
        print_success "地理位置服务启动成功 (PID: $LOCATION_SERVICE_PID)"
        cd ../..
    else
        print_warning "地理位置服务jar包不存在"
    fi
    
    # 启动订单服务
    if [ -f "backend/order-service/target/order-service-1.0.0.jar" ]; then
        print_info "启动订单服务 (端口: 8084)..."
        cd backend/order-service
        nohup java -jar target/order-service-1.0.0.jar > ../../logs/order-service.log 2>&1 &
        ORDER_SERVICE_PID=$!
        echo $ORDER_SERVICE_PID > ../../logs/order-service.pid
        print_success "订单服务启动成功 (PID: $ORDER_SERVICE_PID)"
        cd ../..
    else
        print_warning "订单服务jar包不存在"
    fi
    
    # 等待服务启动
    print_info "等待服务启动..."
    sleep 15
    
    # 检查服务健康状态
    check_service_health
    
    echo ""
}

# 检查服务健康状态
check_service_health() {
    print_info "检查服务健康状态..."
    
    # 检查用户服务
    if curl -s http://localhost:8081/api/v1/v3/api-docs &>/dev/null; then
        print_success "用户服务运行正常"
    else
        print_warning "用户服务可能未正常启动"
    fi
    
    # 检查商品服务
    if curl -s http://localhost:8082/api/v1/v3/api-docs &>/dev/null; then
        print_success "商品服务运行正常"
    else
        print_warning "商品服务可能未正常启动"
    fi
    
    # 检查地理位置服务
    if curl -s http://localhost:8083/api/v1/v3/api-docs &>/dev/null; then
        print_success "地理位置服务运行正常"
    else
        print_warning "地理位置服务可能未正常启动"
    fi
    
    # 检查订单服务
    if curl -s http://localhost:8084/api/v1/v3/api-docs &>/dev/null; then
        print_success "订单服务运行正常"
    else
        print_warning "订单服务可能未正常启动"
    fi
}

# 显示访问信息
show_access_info() {
    print_step "6" "系统访问信息"
    
    echo -e "${CYAN}后端服务地址:${NC}"
    echo "  用户服务: http://localhost:8081/api/v1"
    echo "  商品服务: http://localhost:8082/api/v1"
    echo "  地理位置服务: http://localhost:8083/api/v1"
    echo "  订单服务: http://localhost:8084/api/v1"
    echo ""
    
    echo -e "${CYAN}API文档地址:${NC}"
    echo "  用户服务: http://localhost:8081/api/v1/swagger-ui.html"
    echo "  商品服务: http://localhost:8082/api/v1/swagger-ui.html"
    echo "  地理位置服务: http://localhost:8083/api/v1/swagger-ui.html"
    echo "  订单服务: http://localhost:8084/api/v1/swagger-ui.html"
    echo ""
    
    echo -e "${CYAN}前端项目:${NC}"
    echo "  位置: frontend/mini-program/"
    echo "  使用HBuilderX打开并运行"
    echo "  或使用命令行: npm run dev:h5"
    echo ""
    
    echo -e "${CYAN}测试账户:${NC}"
    echo "  超级管理员: root / root123"
    echo "  测试用户: testuser / password123"
    echo "  管理员: bjadmin / password123"
    echo ""
    
    echo -e "${CYAN}数据库信息:${NC}"
    echo "  数据库名: lingli_dev"
    echo "  用户名: root"
    echo "  密码: root"
    echo "  端口: 3306"
    echo ""
    
    echo -e "${CYAN}核心功能接口:${NC}"
    echo "  用户登录: POST /api/v1/auth/login"
    echo "  获取分类: GET /api/v1/products/categories/top"
    echo "  获取套餐: GET /api/v1/products/packages"
    echo "  加入购物车: POST /api/v1/cart/add"
    echo "  获取购物车: GET /api/v1/cart/list"
    echo "  创建订单: POST /api/v1/orders/create"
    echo "  获取城市: GET /api/v1/location/cities"
    echo "  设置位置: POST /api/v1/location/user-location"
    echo ""
}

# 显示核心功能
show_features() {
    print_step "7" "系统核心功能"
    
    echo -e "${GREEN}✅ 已实现的核心功能:${NC}"
    echo ""
    echo "📍 地理位置检测与城市管理系统"
    echo "  • IP地址地理位置检测"
    echo "  • 用户城市选择与管理"
    echo "  • 城市与公司关联管理"
    echo "  • 就近城市公司匹配"
    echo ""
    
    echo "🛒 完整订单流程"
    echo "  • 购物车登录检查机制"
    echo "  • 购物车与订单状态管理"
    echo "  • 联系人信息填写"
    echo "  • 采样方式选择 (自邮寄/上门预约)"
    echo "  • 检测人信息管理"
    echo "  • 地理位置关联订单"
    echo ""
    
    echo "💳 支付系统"
    echo "  • 多种支付方式支持"
    echo "  • 支付状态跟踪"
    echo "  • 支付回调处理"
    echo ""
    
    echo "🎯 用户端小程序"
    echo "  • 美观的商城主页设计"
    echo "  • 分类展示与筛选"
    echo "  • 购物车登录检查"
    echo "  • 订单流程管理"
    echo "  • 地理位置设置"
    echo ""
    
    echo -e "${YELLOW}🔧 技术架构:${NC}"
    echo "  • 微服务架构 (用户/商品/地理位置/订单服务)"
    echo "  • 统一认证授权 (JWT)"
    echo "  • 缓存支持 (Redis)"
    echo "  • 数据库设计 (MySQL)"
    echo "  • API文档 (Swagger)"
    echo ""
}

# 性能优化建议
show_optimization_tips() {
    print_step "8" "性能优化建议"
    
    echo -e "${BLUE}🔧 系统优化建议:${NC}"
    echo ""
    echo "1. 生产环境配置"
    echo "   • 配置生产数据库连接"
    echo "   • 启用Redis集群"
    echo "   • 配置负载均衡"
    echo "   • 开启应用监控"
    echo ""
    
    echo "2. 安全性配置"
    echo "   • 更换JWT密钥"
    echo "   • 配置HTTPS证书"
    echo "   • 启用API限流"
    echo "   • 配置数据库安全"
    echo ""
    
    echo "3. 扩展性配置"
    echo "   • 配置服务发现 (Eureka/Consul)"
    echo "   • 启用容器化部署 (Docker)"
    echo "   • 配置CI/CD流水线"
    echo "   • 设置监控告警"
    echo ""
}

# 主函数
main() {
    case "${1:-deploy}" in
        "deploy")
            print_header
            show_system_info
            
            if ! check_dependencies; then
                print_error "环境依赖检查失败，请先安装必要的软件"
                exit 1
            fi
            
            start_basic_services
            init_database
            build_backend
            start_backend_services
            show_access_info
            show_features
            show_optimization_tips
            
            echo -e "${GREEN}============================================${NC}"
            echo -e "${GREEN}  系统部署完成！🎉${NC}"
            echo -e "${GREEN}============================================${NC}"
            echo ""
            print_info "系统已成功部署并启动！"
            print_info "现在可以："
            echo "  1. 访问API文档测试接口"
            echo "  2. 使用HBuilderX运行前端项目"
            echo "  3. 查看系统日志了解运行状态"
            echo ""
            print_info "如需停止服务，请运行: $0 stop"
            ;;
        "check")
            print_header
            check_dependencies
            ;;
        "build")
            print_header
            build_backend
            ;;
        "start")
            print_header
            start_backend_services
            show_access_info
            ;;
        "stop")
            print_header
            print_step "停止所有服务"
            
            # 停止所有Java服务
            if [ -f "logs/user-service.pid" ]; then
                USER_PID=$(cat logs/user-service.pid)
                if kill -0 $USER_PID 2>/dev/null; then
                    kill $USER_PID
                    print_success "用户服务已停止"
                fi
                rm -f logs/user-service.pid
            fi
            
            if [ -f "logs/product-service.pid" ]; then
                PRODUCT_PID=$(cat logs/product-service.pid)
                if kill -0 $PRODUCT_PID 2>/dev/null; then
                    kill $PRODUCT_PID
                    print_success "商品服务已停止"
                fi
                rm -f logs/product-service.pid
            fi
            
            if [ -f "logs/location-service.pid" ]; then
                LOCATION_PID=$(cat logs/location-service.pid)
                if kill -0 $LOCATION_PID 2>/dev/null; then
                    kill $LOCATION_PID
                    print_success "地理位置服务已停止"
                fi
                rm -f logs/location-service.pid
            fi
            
            if [ -f "logs/order-service.pid" ]; then
                ORDER_PID=$(cat logs/order-service.pid)
                if kill -0 $ORDER_PID 2>/dev/null; then
                    kill $ORDER_PID
                    print_success "订单服务已停止"
                fi
                rm -f logs/order-service.pid
            fi
            
            # 杀死所有相关Java进程
            pkill -f "lingli.*service" 2>/dev/null || true
            
            print_success "所有服务已停止"
            ;;
        "restart")
            $0 stop
            sleep 5
            $0 start
            ;;
        "status")
            print_header
            check_service_health
            ;;
        "help"|"--help"|"-h")
            echo "灵力检测实验室系统部署脚本"
            echo ""
            echo "用法: $0 [选项]"
            echo ""
            echo "选项:"
            echo "  deploy   完整部署流程 (推荐)"
            echo "  check    仅检查环境依赖"
            echo "  build    仅编译后端项目"
            echo "  start    仅启动后端服务"
            echo "  stop     停止所有服务"
            echo "  restart  重启服务"
            echo "  status   检查服务状态"
            echo "  help     显示帮助信息"
            echo ""
            echo "示例:"
            echo "  $0 deploy     # 完整部署"
            echo "  $0 start      # 启动服务"
            echo "  $0 stop       # 停止服务"
            echo "  $0 status     # 检查状态"
            echo ""
            ;;
        *)
            print_error "未知选项: $1"
            print_info "使用 '$0 help' 查看帮助信息"
            exit 1
            ;;
    esac
}

# 脚本入口
main "$@"