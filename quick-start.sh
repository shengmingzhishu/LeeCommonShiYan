#!/bin/bash

# 灵力检测实验室系统快速启动脚本
# 提供真正可运行的最小版本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印函数
print_header() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}  灵力检测实验室系统快速启动${NC}"
    echo -e "${BLUE}  提供真正可运行的最小版本${NC}"
    echo -e "${BLUE}============================================${NC}"
    echo ""
}

print_step() {
    echo -e "${YELLOW}[步骤 $1]${NC} $2"
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

# 检查系统依赖
check_dependencies() {
    print_step "1" "检查系统依赖"
    
    # 检查Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1)
        print_success "Java已安装: $JAVA_VERSION"
    else
        print_error "Java未安装，请先安装Java 11+"
        echo "  Ubuntu/Debian: sudo apt install openjdk-11-jdk"
        echo "  CentOS/RHEL: sudo yum install java-11-openjdk-devel"
        echo "  macOS: brew install openjdk@11"
        exit 1
    fi
    
    # 检查Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n1)
        print_success "Maven已安装: $MVN_VERSION"
    else
        print_error "Maven未安装，请先安装Maven 3.6+"
        echo "  下载地址: https://maven.apache.org/download.cgi"
        exit 1
    fi
    
    # 检查MySQL
    if command -v mysql &> /dev/null; then
        MYSQL_VERSION=$(mysql --version 2>/dev/null || echo "MySQL客户端工具已安装")
        print_success "MySQL已安装: $MYSQL_VERSION"
        print_warning "请确保MySQL服务正在运行"
    else
        print_warning "MySQL客户端工具未找到，请确保MySQL服务正在运行"
    fi
    
    # 检查Redis
    if command -v redis-cli &> /dev/null; then
        REDIS_VERSION=$(redis-cli --version 2>/dev/null || echo "Redis客户端工具已安装")
        print_success "Redis已安装: $REDIS_VERSION"
        print_warning "请确保Redis服务正在运行"
    else
        print_warning "Redis客户端工具未找到，请确保Redis服务正在运行"
    fi
    
    echo ""
}

# 编译并启动用户服务
start_user_service() {
    print_step "2" "编译并启动用户服务"
    
    if [ -f "backend/user-service/pom.xml" ]; then
        cd backend/user-service
        
        print_info "清理并编译用户服务..."
        mvn clean compile -q
        
        if [ $? -eq 0 ]; then
            print_success "用户服务编译成功"
            
            print_info "启动用户服务..."
            # 后台启动服务
            nohup java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.lingli.user.UserServiceApplication > ../../logs/user-service.log 2>&1 &
            USER_PID=$!
            echo $USER_PID > ../../logs/user-service.pid
            
            # 等待服务启动
            print_info "等待服务启动..."
            sleep 10
            
            # 检查服务状态
            if curl -s http://localhost:8081/api/v1/v3/api-docs &>/dev/null; then
                print_success "用户服务启动成功！"
                cd ../..
            else
                print_warning "用户服务可能未正常启动"
                cd ../..
            fi
        else
            print_error "用户服务编译失败"
            cd ../..
            exit 1
        fi
    else
        print_error "用户服务不存在，请确保项目结构正确"
        exit 1
    fi
    
    echo ""
}

# 编译并启动商品服务
start_product_service() {
    print_step "3" "编译并启动商品服务"
    
    if [ -f "backend/product-service/pom.xml" ]; then
        cd backend/product-service
        
        print_info "清理并编译商品服务..."
        mvn clean compile -q
        
        if [ $? -eq 0 ]; then
            print_success "商品服务编译成功"
            
            print_info "启动商品服务..."
            # 后台启动服务
            nohup java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.lingli.product.ProductServiceApplication > ../../logs/product-service.log 2>&1 &
            PRODUCT_PID=$!
            echo $PRODUCT_PID > ../../logs/product-service.pid
            
            # 等待服务启动
            print_info "等待服务启动..."
            sleep 8
            
            # 检查服务状态
            if curl -s http://localhost:8082/api/v1/v3/api-docs &>/dev/null; then
                print_success "商品服务启动成功！"
                cd ../..
            else
                print_warning "商品服务可能未正常启动"
                cd ../..
            fi
        else
            print_error "商品服务编译失败"
            cd ../..
            exit 1
        fi
    else
        print_warning "商品服务不存在，跳过"
        cd ../..
    fi
    
    echo ""
}

# 显示访问信息
show_access_info() {
    print_step "4" "系统访问信息"
    
    echo -e "${BLUE}后端服务地址:${NC}"
    echo "  用户服务: http://localhost:8081/api/v1"
    echo "  商品服务: http://localhost:8082/api/v1"
    echo ""
    
    echo -e "${BLUE}API文档地址:${NC}"
    echo "  用户服务: http://localhost:8081/api/v1/swagger-ui.html"
    echo "  商品服务: http://localhost:8082/api/v1/swagger-ui.html"
    echo ""
    
    echo -e "${BLUE}测试接口:${NC}"
    echo "  用户登录: POST http://localhost:8081/api/v1/auth/login"
    echo "  获取套餐: GET http://localhost:8082/api/v1/products/packages"
    echo ""
    
    echo -e "${BLUE}测试账户:${NC}"
    echo "  超级管理员: root / root123"
    echo "  测试用户: testuser / password123"
    echo ""
    
    echo -e "${BLUE}前端项目:${NC}"
    echo "  位置: frontend/mini-program/"
    echo "  使用HBuilderX打开并运行"
    echo ""
    
    echo -e "${BLUE}数据库信息:${NC}"
    echo "  数据库名: lingli_dev"
    echo "  用户名: root"
    echo "  密码: root"
    echo "  端口: 3306"
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

# 主函数
main() {
    case "${1:-start}" in
        "start")
            print_header
            create_logs_dir
            check_dependencies
            start_user_service
            start_product_service
            show_access_info
            
            echo -e "${GREEN}============================================${NC}"
            echo -e "${GREEN}  系统启动完成！${NC}"
            echo -e "${GREEN}  现在可以测试API接口了！${NC}"
            echo -e "${GREEN}============================================${NC}"
            echo ""
            print_info "如需停止服务，请运行: $0 stop"
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
            
            # 杀死所有相关Java进程
            pkill -f "lingli.*service" 2>/dev/null || true
            
            print_success "所有服务已停止"
            ;;
        "check")
            print_header
            check_dependencies
            ;;
        "help"|"--help"|"-h")
            echo "灵力检测实验室系统快速启动脚本"
            echo ""
            echo "用法: $0 [选项]"
            echo ""
            echo "选项:"
            echo "  start     启动系统 (推荐)"
            echo "  stop      停止所有服务"
            echo "  check     仅检查环境依赖"
            echo "  help      显示帮助信息"
            echo ""
            echo "示例:"
            echo "  $0 start      # 启动系统"
            echo "  $0 stop       # 停止服务"
            echo "  $0 check      # 检查环境"
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