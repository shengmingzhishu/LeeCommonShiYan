#!/bin/bash

# 灵力检测实验室API测试脚本
# 提供真实的API测试

echo "=== 灵力检测实验室API测试 ==="
echo ""

# 测试用户服务
echo "1. 测试用户服务..."
echo "   用户登录:"
curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' | head -c 200
echo ""
echo ""

echo "   获取用户信息:"
curl -s http://localhost:8081/api/v1/auth/profile | head -c 200
echo ""
echo ""

# 测试商品服务
echo "2. 测试商品服务..."
echo "   获取套餐列表:"
curl -s http://localhost:8082/api/v1/products/packages | head -c 200
echo ""
echo ""

echo "   获取分类列表:"
curl -s http://localhost:8082/api/v1/products/categories/top | head -c 200
echo ""
echo ""

echo "=== 测试完成 ==="
