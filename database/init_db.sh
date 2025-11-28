#!/bin/bash

# 灵力检测实验室数据库初始化脚本
# 作者: lingli
# 日期: 2023-11-28

set -e

# 配置参数
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"3306"}
DB_USER=${DB_USER:-"root"}
DB_PASSWORD=${DB_PASSWORD:-"root"}
DB_NAME_DEV=${DB_NAME_DEV:-"lingli_dev"}
DB_NAME_TEST=${DB_NAME_TEST:-"lingli_test"}
DB_NAME_PROD=${DB_NAME_PROD:-"lingli_prod"}

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印函数
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查MySQL连接
check_mysql_connection() {
    print_info "检查MySQL连接..."
    if ! mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1" &>/dev/null; then
        print_error "MySQL连接失败，请检查配置"
        exit 1
    fi
    print_info "MySQL连接成功"
}

# 创建数据库
create_database() {
    local db_name=$1
    print_info "创建数据库: $db_name"
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -e "
        CREATE DATABASE IF NOT EXISTS \`$db_name\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
        GRANT ALL PRIVILEGES ON \`$db_name\`.* TO '$DB_USER'@'%';
        FLUSH PRIVILEGES;
    "
    
    print_info "数据库 $db_name 创建成功"
}

# 执行SQL脚本
execute_sql_file() {
    local db_name=$1
    local sql_file=$2
    
    if [ ! -f "$sql_file" ]; then
        print_error "SQL文件不存在: $sql_file"
        exit 1
    fi
    
    print_info "执行SQL文件: $sql_file"
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" "$db_name" < "$sql_file"
    
    if [ $? -eq 0 ]; then
        print_info "SQL文件执行成功: $sql_file"
    else
        print_error "SQL文件执行失败: $sql_file"
        exit 1
    fi
}

# 初始化开发环境数据库
init_dev_database() {
    print_info "开始初始化开发环境数据库..."
    
    # 创建数据库
    create_database "$DB_NAME_DEV"
    
    # 执行表结构创建脚本
    execute_sql_file "$DB_NAME_DEV" "database/init/01_create_tables.sql"
    
    # 执行种子数据脚本
    execute_sql_file "$DB_NAME_DEV" "database/seeds/01_initial_data.sql"
    
    print_info "开发环境数据库初始化完成"
}

# 初始化测试环境数据库
init_test_database() {
    print_info "开始初始化测试环境数据库..."
    
    # 创建数据库
    create_database "$DB_NAME_TEST"
    
    # 执行表结构创建脚本
    execute_sql_file "$DB_NAME_TEST" "database/init/01_create_tables.sql"
    
    # 执行种子数据脚本
    execute_sql_file "$DB_NAME_TEST" "database/seeds/01_initial_data.sql"
    
    print_info "测试环境数据库初始化完成"
}

# 初始化生产环境数据库
init_prod_database() {
    print_warn "生产环境数据库初始化将清除所有现有数据！"
    read -p "确认继续? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "操作已取消"
        exit 0
    fi
    
    print_info "开始初始化生产环境数据库..."
    
    # 创建数据库
    create_database "$DB_NAME_PROD"
    
    # 执行表结构创建脚本
    execute_sql_file "$DB_NAME_PROD" "database/init/01_create_tables.sql"
    
    # 执行种子数据脚本
    execute_sql_file "$DB_NAME_PROD" "database/seeds/01_initial_data.sql"
    
    print_info "生产环境数据库初始化完成"
}

# 显示帮助信息
show_help() {
    echo "灵力检测实验室数据库初始化脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  dev      初始化开发环境数据库"
    echo "  test     初始化测试环境数据库"
    echo "  prod     初始化生产环境数据库"
    echo "  help     显示帮助信息"
    echo ""
    echo "环境变量:"
    echo "  DB_HOST          MySQL主机地址 (默认: localhost)"
    echo "  DB_PORT          MySQL端口 (默认: 3306)"
    echo "  DB_USER          MySQL用户名 (默认: root)"
    echo "  DB_PASSWORD      MySQL密码 (默认: root)"
    echo "  DB_NAME_DEV      开发环境数据库名 (默认: lingli_dev)"
    echo "  DB_NAME_TEST     测试环境数据库名 (默认: lingli_test)"
    echo "  DB_NAME_PROD     生产环境数据库名 (默认: lingli_prod)"
    echo ""
    echo "示例:"
    echo "  $0 dev                           # 初始化开发环境"
    echo "  $0 prod                          # 初始化生产环境"
    echo "  DB_PASSWORD=123456 $0 dev        # 使用自定义密码"
    echo ""
}

# 主函数
main() {
    case "${1:-}" in
        "dev")
            check_mysql_connection
            init_dev_database
            ;;
        "test")
            check_mysql_connection
            init_test_database
            ;;
        "prod")
            check_mysql_connection
            init_prod_database
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        "")
            print_error "请指定要初始化的环境 (dev/test/prod)"
            echo "使用 '$0 help' 查看帮助信息"
            exit 1
            ;;
        *)
            print_error "未知参数: $1"
            echo "使用 '$0 help' 查看帮助信息"
            exit 1
            ;;
    esac
}

# 脚本入口
main "$@"
