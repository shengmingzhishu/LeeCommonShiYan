#!/bin/bash

# 灵力检测实验室系统快速启动脚本
# 作者: lingli
# 日期: 2023-11-28

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印函数
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  灵力检测实验室系统启动脚本${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# 检查Java环境
check_java() {
    print_info "检查Java环境..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java未安装，请先安装Java 11+"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    print_info "Java版本: $JAVA_VERSION"
    
    # 检查Java版本是否满足要求
    if ! java -version 2>&1 | grep -q "1\."; then
        print_warn "建议使用Java 11或更高版本"
    fi
}

# 检查Maven环境
check_maven() {
    print_info "检查Maven环境..."
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven未安装，请先安装Maven 3.6+"
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version | head -n1)
    print_info "Maven版本: $MVN_VERSION"
}

# 检查MySQL服务
check_mysql() {
    print_info "检查MySQL服务..."
    
    if ! command -v mysql &> /dev/null; then
        print_warn "MySQL命令行工具未找到，跳过数据库检查"
        return
    fi
    
    # 尝试连接MySQL
    if mysql -hlocalhost -P3306 -uroot -proot -e "SELECT 1" &>/dev/null; then
        print_success "MySQL服务连接正常"
    else
        print_warn "MySQL连接失败，请确保服务运行正常"
        print_info "默认连接信息: 主机: localhost, 端口: 3306, 用户名: root, 密码: root"
    fi
}

# 检查Redis服务
check_redis() {
    print_info "检查Redis服务..."
    
    if ! command -v redis-cli &> /dev/null; then
        print_warn "Redis命令行工具未找到，跳过Redis检查"
        return
    fi
    
    # 尝试连接Redis
    if redis-cli ping &>/dev/null; then
        print_success "Redis服务连接正常"
    else
        print_warn "Redis连接失败，请确保服务运行正常"
        print_info "默认连接信息: 主机: localhost, 端口: 6379"
    fi
}

# 初始化数据库
init_database() {
    print_info "是否需要初始化数据库？"
    echo "1. 初始化开发环境数据库 (lingli_dev)"
    echo "2. 跳过数据库初始化"
    echo "3. 退出"
    
    read -p "请选择 (1-3): " choice
    
    case $choice in
        1)
            print_info "开始初始化数据库..."
            if [ -f "./database/init_db.sh" ]; then
                chmod +x ./database/init_db.sh
                ./database/init_db.sh dev
                print_success "数据库初始化完成"
            else
                print_error "数据库初始化脚本不存在"
            fi
            ;;
        2)
            print_info "跳过数据库初始化"
            ;;
        3)
            print_info "退出启动脚本"
            exit 0
            ;;
        *)
            print_warn "无效选择，跳过数据库初始化"
            ;;
    esac
}

# 编译项目
build_project() {
    print_info "编译项目..."
    
    if [ -f "backend/pom.xml" ]; then
        cd backend
        
        print_info "正在编译项目，请稍候..."
        mvn clean install -DskipTests -q
        
        if [ $? -eq 0 ]; then
            print_success "项目编译完成"
        else
            print_error "项目编译失败"
            exit 1
        fi
        
        cd ..
    else
        print_error "backend目录或pom.xml文件不存在"
        exit 1
    fi
}

# 启动用户服务
start_user_service() {
    print_info "启动用户服务..."
    
    if [ -f "backend/user-service/target/user-service-1.0.0.jar" ]; then
        cd backend/user-service
        
        print_info "用户服务启动中..."
        java -jar target/user-service-1.0.0.jar &
        
        # 等待服务启动
        sleep 5
        
        # 检查服务是否启动成功
        if curl -s http://localhost:8081/api/v1/v3/api-docs &>/dev/null; then
            print_success "用户服务启动成功！"
            print_info "API文档地址: http://localhost:8081/api/v1/swagger-ui.html"
            print_info "API基础路径: http://localhost:8081/api/v1"
        else
            print_error "用户服务启动可能失败，请检查日志"
        fi
        
        cd ../..
    else
        print_error "用户服务jar包不存在，请先编译项目"
        exit 1
    fi
}

# 显示帮助信息
show_help() {
    print_header
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  start     快速启动系统"
    echo "  help      显示帮助信息"
    echo ""
    echo "功能:"
    echo "  - 检查Java、Maven环境"
    echo "  - 检查MySQL、Redis服务"
    echo "  - 初始化数据库"
    echo "  - 编译项目"
    echo "  - 启动用户服务"
    echo ""
    echo "示例:"
    echo "  $0 start"
    echo ""
}

# 显示系统信息
show_system_info() {
    print_info "系统信息:"
    echo "  操作系统: $(uname -s)"
    echo "  架构: $(uname -m)"
    echo "  当前目录: $(pwd)"
    echo "  脚本路径: $0"
    echo ""
}

# 主函数
main() {
    print_header
    show_system_info
    
    case "${1:-}" in
        "start")
            check_java
            check_maven
            check_mysql
            check_redis
            init_database
            build_project
            start_user_service
            
            print_success "系统启动完成！"
            echo ""
            print_info "默认测试账户:"
            echo "  超级管理员: root / root123"
            echo "  测试用户: testuser / password123"
            echo "  管理员: bjadmin / password123"
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        "")
            print_error "请指定要执行的操作"
            print_info "使用 '$0 help' 查看帮助信息"
            exit 1
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