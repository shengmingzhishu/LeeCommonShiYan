#!/bin/bash

# 灵力检测实验室系统完整启动指南
# 作者: lingli
# 日期: 2023-11-28

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
    echo -e "${BLUE}  灵力检测实验室系统启动指南${NC}"
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

# 检查系统要求
check_requirements() {
    print_step "1" "检查系统要求"
    
    # 检查Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1)
        print_success "Java已安装: $JAVA_VERSION"
    else
        print_error "Java未安装，请先安装Java 11+"
        echo "  Ubuntu/Debian: sudo apt install openjdk-11-jdk"
        echo "  CentOS/RHEL: sudo yum install java-11-openjdk-devel"
        echo "  macOS: brew install openjdk@11"
    fi
    
    # 检查Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n1)
        print_success "Maven已安装: $MVN_VERSION"
    else
        print_error "Maven未安装，请先安装Maven 3.6+"
        echo "  下载地址: https://maven.apache.org/download.cgi"
        echo "  环境变量: export PATH=\$PATH:/path/to/maven/bin"
    fi
    
    # 检查MySQL
    if command -v mysql &> /dev/null; then
        MYSQL_VERSION=$(mysql --version 2>/dev/null || echo "MySQL客户端工具已安装")
        print_success "MySQL已安装: $MYSQL_VERSION"
    else
        print_warning "MySQL客户端工具未找到，请确保MySQL服务正常运行"
    fi
    
    # 检查Redis
    if command -v redis-cli &> /dev/null; then
        REDIS_VERSION=$(redis-cli --version 2>/dev/null || echo "Redis客户端工具已安装")
        print_success "Redis已安装: $REDIS_VERSION"
    else
        print_warning "Redis客户端工具未找到，请确保Redis服务正常运行"
    fi
    
    echo ""
}

# 启动数据库服务
start_database() {
    print_step "2" "启动数据库服务"
    
    # 启动MySQL
    if command -v systemctl &> /dev/null; then
        print_info "尝试启动MySQL服务..."
        sudo systemctl start mysql 2>/dev/null || sudo systemctl start mysqld 2>/dev/null || {
            print_warning "MySQL服务启动失败，请手动启动MySQL"
            echo "  Ubuntu/Debian: sudo systemctl start mysql"
            echo "  CentOS/RHEL: sudo systemctl start mysqld"
            echo "  macOS: brew services start mysql"
        }
    fi
    
    # 启动Redis
    if command -v systemctl &> /dev/null; then
        print_info "尝试启动Redis服务..."
        sudo systemctl start redis 2>/dev/null || sudo systemctl start redis-server 2>/dev/null || {
            print_warning "Redis服务启动失败，请手动启动Redis"
            echo "  Ubuntu/Debian: sudo systemctl start redis"
            echo "  CentOS/RHEL: sudo systemctl start redis"
            echo "  macOS: brew services start redis"
        }
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
        fi
    else
        print_error "数据库初始化脚本不存在"
        echo "请确保在项目根目录下运行此脚本"
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
        
        print_info "编译用户服务..."
        mvn clean install -DskipTests -pl user-service -q
        
        print_info "编译商品服务..."
        mvn clean install -DskipTests -pl product-service -q
        
        print_success "后端项目编译完成"
        cd ..
    else
        print_error "后端项目目录不存在"
    fi
    
    echo ""
}

# 启动后端服务
start_backend_services() {
    print_step "5" "启动后端服务"
    
    # 启动用户服务
    if [ -f "backend/user-service/target/user-service-1.0.0.jar" ]; then
        print_info "启动用户服务 (端口: 8081)..."
        cd backend/user-service
        
        # 后台启动服务
        nohup java -jar target/user-service-1.0.0.jar > ../../logs/user-service.log 2>&1 &
        USER_SERVICE_PID=$!
        echo $USER_SERVICE_PID > ../../logs/user-service.pid
        
        print_success "用户服务启动成功 (PID: $USER_SERVICE_PID)"
        cd ../..
    else
        print_error "用户服务jar包不存在，请先编译项目"
    fi
    
    # 启动商品服务
    if [ -f "backend/product-service/target/product-service-1.0.0.jar" ]; then
        print_info "启动商品服务 (端口: 8082)..."
        cd backend/product-service
        
        # 后台启动服务
        nohup java -jar target/product-service-1.0.0.jar > ../../logs/product-service.log 2>&1 &
        PRODUCT_SERVICE_PID=$!
        echo $PRODUCT_SERVICE_PID > ../../logs/product-service.pid
        
        print_success "商品服务启动成功 (PID: $PRODUCT_SERVICE_PID)"
        cd ../..
    else
        print_error "商品服务jar包不存在，请先编译项目"
    fi
    
    # 等待服务启动
    print_info "等待服务启动..."
    sleep 10
    
    # 检查服务状态
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
}

# 显示访问信息
show_access_info() {
    print_step "6" "系统访问信息"
    
    echo -e "${CYAN}后端服务地址:${NC}"
    echo "  用户服务: http://localhost:8081/api/v1"
    echo "  商品服务: http://localhost:8082/api/v1"
    echo "  API文档: http://localhost:8081/api/v1/swagger-ui.html (用户服务)"
    echo "  API文档: http://localhost:8082/api/v1/swagger-ui.html (商品服务)"
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
    
    echo -e "${CYAN}重要接口:${NC}"
    echo "  用户登录: POST /api/v1/auth/login"
    echo "  获取分类: GET /api/v1/products/categories/top"
    echo "  获取套餐: GET /api/v1/products/packages"
    echo ""
}

# 创建日志目录
create_logs_dir() {
    print_step "0" "创建日志目录"
    
    if [ ! -d "logs" ]; then
        mkdir -p logs
        print_success "日志目录已创建"
    else
        print_success "日志目录已存在"
    fi
    
    echo ""
}

# 显示帮助信息
show_help() {
    print_header
    echo "灵力检测实验室系统启动脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  start     完整启动流程 (推荐)"
    echo "  check     仅检查系统要求"
    echo "  db        仅初始化数据库"
    echo "  build     仅编译后端项目"
    echo "  services  仅启动后端服务"
    echo "  help      显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 start      # 完整启动"
    echo "  $0 check      # 检查环境"
    echo "  $0 build      # 编译项目"
    echo ""
}

# 主函数
main() {
    case "${1:-start}" in
        "start")
            print_header
            create_logs_dir
            check_requirements
            start_database
            init_database
            build_backend
            start_backend_services
            show_access_info
            
            echo -e "${GREEN}============================================${NC}"
            echo -e "${GREEN}  系统启动完成！${NC}"
            echo -e "${GREEN}============================================${NC}"
            echo ""
            print_info "如需停止服务，请运行: $0 stop"
            ;;
        "check")
            print_header
            check_requirements
            ;;
        "db")
            print_header
            init_database
            ;;
        "build")
            print_header
            build_backend
            ;;
        "services")
            print_header
            create_logs_dir
            start_backend_services
            show_access_info
            ;;
        "stop")
            print_header
            print_step "停止服务"
            
            # 停止用户服务
            if [ -f "logs/user-service.pid" ]; then
                USER_PID=$(cat logs/user-service.pid)
                if kill -0 $USER_PID 2>/dev/null; then
                    kill $USER_PID
                    print_success "用户服务已停止"
                fi
                rm -f logs/user-service.pid
            fi
            
            # 停止商品服务
            if [ -f "logs/product-service.pid" ]; then
                PRODUCT_PID=$(cat logs/product-service.pid)
                if kill -0 $PRODUCT_PID 2>/dev/null; then
                    kill $PRODUCT_PID
                    print_success "商品服务已停止"
                fi
                rm -f logs/product-service.pid
            fi
            
            print_success "所有服务已停止"
            ;;
        "help"|"--help"|"-h")
            show_help
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