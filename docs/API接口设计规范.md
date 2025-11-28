# 灵力检测实验室 API接口设计规范

## 1. API设计原则

### 1.1 RESTful设计风格
- 使用标准HTTP方法：GET、POST、PUT、DELETE
- 资源名使用名词复数形式
- URL层次清晰，语义明确
- 版本控制通过URL路径实现

### 1.2 接口规范
- 所有接口统一使用UTF-8编码
- 时间格式统一使用ISO 8601标准 (yyyy-MM-dd'T'HH:mm:ss'Z')
- 金额统一使用分作为单位，避免浮点数精度问题
- 日期格式：yyyy-MM-dd
- 时间戳：10位Unix时间戳

### 1.3 状态码规范
- 200: 成功
- 400: 请求参数错误
- 401: 未授权/Token无效
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

## 2. 统一响应格式

### 2.1 成功响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {
        // 具体业务数据
    },
    "timestamp": 1635724800000
}
```

### 2.2 失败响应格式
```json
{
    "code": 400,
    "message": "请求参数错误",
    "data": null,
    "errors": [
        {
            "field": "phone",
            "message": "手机号格式不正确"
        }
    ],
    "timestamp": 1635724800000
}
```

### 2.3 分页响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [
            // 记录列表
        ],
        "total": 100,
        "size": 10,
        "current": 1,
        "pages": 10
    },
    "timestamp": 1635724800000
}
```

## 3. 错误码设计

### 3.1 系统级错误码
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

### 3.2 业务级错误码
```java
public class ErrorCode {
    // 用户相关
    public static final int USER_NOT_FOUND = 10001;
    public static final int USER_EXIST = 10002;
    public static final int USER_DISABLED = 10003;
    public static final int PASSWORD_ERROR = 10004;
    public static final int TOKEN_INVALID = 10005;
    public static final int TOKEN_EXPIRED = 10006;
    
    // 商品相关
    public static final int PACKAGE_NOT_FOUND = 20001;
    public static final int PACKAGE_OFFLINE = 20002;
    public static final int INSUFFICIENT_STOCK = 20003;
    
    // 订单相关
    public static final int ORDER_NOT_FOUND = 30001;
    public static final int ORDER_CANNOT_CANCEL = 30002;
    public static final int ORDER_STATUS_ERROR = 30003;
    
    // 支付相关
    public static final int PAYMENT_FAILED = 40001;
    public static final int PAYMENT_TIMEOUT = 40002;
    public static final int DUPLICATE_PAYMENT = 40003;
    
    // 权限相关
    public static final int PERMISSION_DENIED = 50001;
    public static final int ROLE_INSUFFICIENT = 50002;
}
```

## 4. 安全认证

### 4.1 JWT Token认证
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4.2 Token刷新机制
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
    "refreshToken": "refresh_token_here"
}
```

### 4.3 接口权限注解
```java
@PreAuthorize("hasRole('USER')")
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('COMPANY_ADMIN')")
```

## 5. 接口详细设计

### 5.1 用户管理接口

#### 5.1.1 用户注册
```http
POST /api/v1/auth/register
Content-Type: application/json

{
    "username": "user001",
    "password": "password123",
    "phone": "13812345678",
    "email": "user@example.com"
}

Response:
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "userId": 1,
        "token": "jwt_token",
        "refreshToken": "refresh_token"
    }
}
```

#### 5.1.2 用户登录
```http
POST /api/v1/auth/login
Content-Type: application/json

{
    "username": "user001",
    "password": "password123"
}

Response:
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "userId": 1,
        "username": "user001",
        "phone": "13812345678",
        "token": "jwt_token",
        "refreshToken": "refresh_token",
        "expiresIn": 7200
    }
}
```

#### 5.1.3 获取用户信息
```http
GET /api/v1/users/profile
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "user001",
        "phone": "13812345678",
        "email": "user@example.com",
        "realName": "张三",
        "gender": 1,
        "birthDate": "1990-01-01"
    }
}
```

#### 5.1.4 更新用户资料
```http
PUT /api/v1/users/profile
Authorization: Bearer {token}
Content-Type: application/json

{
    "realName": "张三",
    "gender": 1,
    "birthDate": "1990-01-01",
    "address": "北京市朝阳区xxx",
    "emergencyContact": "李四",
    "emergencyPhone": "13912345678"
}

Response:
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

#### 5.1.5 采样人管理
```http
# 添加采样人
POST /api/v1/users/samplers
Authorization: Bearer {token}
Content-Type: application/json

{
    "name": "王五",
    "gender": 2,
    "age": 25,
    "phone": "13712345678",
    "idCard": "110101199901010001",
    "address": "北京市海淀区xxx",
    "medicalHistory": "无特殊病史"
}

# 获取采样人列表
GET /api/v1/users/samplers
Authorization: Bearer {token}

# 更新采样人信息
PUT /api/v1/users/samplers/{samplerId}
Authorization: Bearer {token}

# 删除采样人
DELETE /api/v1/users/samplers/{samplerId}
Authorization: Bearer {token}
```

### 5.2 商品管理接口

#### 5.2.1 获取套餐分类列表
```http
GET /api/v1/categories

Response:
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "基础体检",
            "code": "BASIC",
            "parentId": 0,
            "sortOrder": 1,
            "children": [
                {
                    "id": 2,
                    "name": "入职体检",
                    "code": "PRE_EMPLOYMENT",
                    "parentId": 1,
                    "sortOrder": 1
                }
            ]
        }
    ]
}
```

#### 5.2.2 获取套餐列表
```http
GET /api/v1/packages?categoryId=1&keyword=体检&page=1&size=10

Query Parameters:
- categoryId: 分类ID（可选）
- keyword: 搜索关键词（可选）
- page: 页码，默认1
- size: 每页数量，默认10

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [
            {
                "id": 1,
                "name": "基础入职体检套餐",
                "code": "BASIC_PRE_EMPLOYMENT",
                "price": 29900,
                "originalPrice": 39900,
                "coverImage": "https://cdn.example.com/image1.jpg",
                "testItems": ["血常规", "尿常规", "胸透"],
                "samplingMethod": 3,
                "reportDeliveryDays": 3,
                "description": "包含基础检查项目，适合入职体检..."
            }
        ],
        "total": 20,
        "size": 10,
        "current": 1,
        "pages": 2
    }
}
```

#### 5.2.3 获取套餐详情
```http
GET /api/v1/packages/{packageId}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "categoryId": 1,
        "name": "基础入职体检套餐",
        "code": "BASIC_PRE_EMPLOYMENT",
        "price": 29900,
        "originalPrice": 39900,
        "coverImage": "https://cdn.example.com/image1.jpg",
        "detailImages": [
            "https://cdn.example.com/detail1.jpg",
            "https://cdn.example.com/detail2.jpg"
        ],
        "testItems": ["血常规", "尿常规", "胸透", "心电图"],
        "samplingMethod": 3,
        "reportDeliveryDays": 3,
        "stock": 999,
        "description": "套餐详细描述...",
        "status": 1
    }
}
```

### 5.3 购物车接口

#### 5.3.1 添加到购物车
```http
POST /api/v1/cart/add
Authorization: Bearer {token}
Content-Type: application/json

{
    "packageId": 1,
    "quantity": 1
}

Response:
{
    "code": 200,
    "message": "添加成功",
    "data": {
        "cartId": 1,
        "totalItems": 1
    }
}
```

#### 5.3.2 获取购物车列表
```http
GET /api/v1/cart/list
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "packageId": 1,
            "packageName": "基础入职体检套餐",
            "packagePrice": 29900,
            "quantity": 1,
            "coverImage": "https://cdn.example.com/image1.jpg"
        }
    ]
}
```

#### 5.3.3 更新购物车数量
```http
PUT /api/v1/cart/{cartId}
Authorization: Bearer {token}
Content-Type: application/json

{
    "quantity": 2
}

Response:
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

#### 5.3.4 删除购物车商品
```http
DELETE /api/v1/cart/{cartId}
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

### 5.4 订单管理接口

#### 5.4.1 创建订单
```http
POST /api/v1/orders/create
Authorization: Bearer {token}
Content-Type: application/json

{
    "cartIds": [1, 2],
    "shippingType": 1,
    "shippingAddress": "北京市朝阳区xxx",
    "contactName": "张三",
    "contactPhone": "13812345678",
    "remark": "加急处理"
}

Response:
{
    "code": 200,
    "message": "订单创建成功",
    "data": {
        "orderId": 1,
        "orderNo": "ORD2023112800001",
        "totalAmount": 59800,
        "status": 1,
        "payUrl": "https://pay.example.com/pay?orderId=1"
    }
}
```

#### 5.4.2 获取订单列表
```http
GET /api/v1/orders?status=1&page=1&size=10
Authorization: Bearer {token}

Query Parameters:
- status: 订单状态（可选）
- page: 页码，默认1
- size: 每页数量，默认10

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [
            {
                "id": 1,
                "orderNo": "ORD2023112800001",
                "totalAmount": 29900,
                "status": 1,
                "payStatus": 1,
                "shippingType": 1,
                "createdAt": "2023-11-28T10:30:00Z",
                "items": [
                    {
                        "id": 1,
                        "packageName": "基础入职体检套餐",
                        "quantity": 1,
                        "packagePrice": 29900,
                        "samplerName": "张三"
                    }
                ]
            }
        ],
        "total": 5,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

#### 5.4.3 获取订单详情
```http
GET /api/v1/orders/{orderId}
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "orderNo": "ORD2023112800001",
        "totalAmount": 29900,
        "paidAmount": 29900,
        "status": 3,
        "payStatus": 3,
        "payType": "WECHAT_PAY",
        "payTime": "2023-11-28T10:35:00Z",
        "shippingType": 1,
        "shippingAddress": "北京市朝阳区xxx",
        "contactName": "张三",
        "contactPhone": "13812345678",
        "remark": "加急处理",
        "createdAt": "2023-11-28T10:30:00Z",
        "items": [
            {
                "id": 1,
                "packageId": 1,
                "packageName": "基础入职体检套餐",
                "quantity": 1,
                "unitPrice": 29900,
                "totalPrice": 29900,
                "samplerName": "张三",
                "samplerIdCard": "110101199001010001",
                "samplerPhone": "13812345678",
                "samplerAddress": "北京市朝阳区xxx"
            }
        ],
        "logistics": {
            "companyName": "顺丰快递",
            "trackingNo": "SF1234567890",
            "status": 2,
            "timeline": [
                {
                    "status": "已发货",
                    "time": "2023-11-28T15:30:00Z",
                    "description": "快件已在北京朝阳区发出"
                }
            ]
        },
        "reports": [
            {
                "id": 1,
                "fileName": "检验报告_张三.pdf",
                "fileUrl": "https://cdn.example.com/reports/1.pdf",
                "fileType": "pdf",
                "uploadTime": "2023-11-29T14:30:00Z"
            }
        ]
    }
}
```

#### 5.4.4 取消订单
```http
PUT /api/v1/orders/{orderId}/cancel
Authorization: Bearer {token}
Content-Type: application/json

{
    "reason": "用户主动取消"
}

Response:
{
    "code": 200,
    "message": "订单取消成功",
    "data": null
}
```

### 5.5 支付接口

#### 5.5.1 获取支付信息
```http
GET /api/v1/payments/{orderId}/info
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "orderId": 1,
        "orderNo": "ORD2023112800001",
        "amount": 29900,
        "payMethods": [
            {
                "type": "WECHAT_PAY",
                "name": "微信支付",
                "icon": "https://cdn.example.com/icons/wechat.png"
            },
            {
                "type": "ALIPAY",
                "name": "支付宝",
                "icon": "https://cdn.example.com/icons/alipay.png"
            }
        ]
    }
}
```

#### 5.5.2 发起支付
```http
POST /api/v1/payments/{orderId}/pay
Authorization: Bearer {token}
Content-Type: application/json

{
    "payType": "WECHAT_PAY"
}

Response:
{
    "code": 200,
    "message": "支付信息生成成功",
    "data": {
        "payUrl": "https://pay.example.com/wechat?orderId=1&amount=29900",
        "qrCode": "https://cdn.example.com/qrcode/wechat_1.png",
        "expireTime": "2023-11-28T11:00:00Z"
    }
}
```

#### 5.5.3 支付状态查询
```http
GET /api/v1/payments/{orderId}/status
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "orderId": 1,
        "payStatus": 2,
        "paidAmount": 29900,
        "payTime": "2023-11-28T10:35:00Z",
        "transactionId": "wx_20231128_001"
    }
}
```

### 5.6 物流接口

#### 5.6.1 获取物流信息
```http
GET /api/v1/logistics/{orderId}
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "type": 1,
            "companyName": "顺丰快递",
            "trackingNo": "SF1234567890",
            "status": 3,
            "shippedAt": "2023-11-28T15:30:00Z",
            "deliveredAt": null,
            "timeline": [
                {
                    "status": "已发货",
                    "time": "2023-11-28T15:30:00Z",
                    "description": "快件已在北京朝阳区发出"
                },
                {
                    "status": "运输中",
                    "time": "2023-11-28T20:15:00Z",
                    "description": "快件正在运输中"
                }
            ]
        }
    ]
}
```

#### 5.6.2 预约上门取样
```http
POST /api/v1/logistics/sampling-appointment
Authorization: Bearer {token}
Content-Type: application/json

{
    "orderId": 1,
    "appointmentDate": "2023-12-01",
    "appointmentTimeSlot": "09:00-12:00",
    "address": "北京市朝阳区xxx",
    "contactName": "张三",
    "contactPhone": "13812345678",
    "remark": "请提前电话联系"
}

Response:
{
    "code": 200,
    "message": "预约成功",
    "data": {
        "appointmentId": 1,
        "appointmentCode": "APT2023112800001",
        "status": 1
    }
}
```

### 5.7 报告接口

#### 5.7.1 获取报告列表
```http
GET /api/v1/reports?orderId=1&page=1&size=10
Authorization: Bearer {token}

Query Parameters:
- orderId: 订单ID（可选）
- page: 页码，默认1
- size: 每页数量，默认10

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [
            {
                "id": 1,
                "orderId": 1,
                "orderItemId": 1,
                "samplerName": "张三",
                "fileName": "检验报告_张三.pdf",
                "fileUrl": "https://cdn.example.com/reports/1.pdf",
                "fileType": "pdf",
                "fileSize": 1024000,
                "reportType": 1,
                "uploadTime": "2023-11-29T14:30:00Z"
            }
        ],
        "total": 5,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

#### 5.7.2 下载报告
```http
GET /api/v1/reports/{reportId}/download
Authorization: Bearer {token}

Response: 
文件流下载
```

## 6. 管理后台接口

### 6.1 城市管理
```http
# 获取城市列表
GET /api/v1/admin/cities

# 创建城市
POST /api/v1/admin/cities
Content-Type: application/json

{
    "name": "北京市",
    "code": "BEIJING",
    "province": "北京市"
}

# 更新城市
PUT /api/v1/admin/cities/{cityId}

# 删除城市
DELETE /api/v1/admin/cities/{cityId}
```

### 6.2 公司管理
```http
# 获取公司列表
GET /api/v1/admin/companies?cityId=1&status=1&page=1&size=10

# 创建公司
POST /api/v1/admin/companies
Content-Type: application/json

{
    "cityId": 1,
    "name": "北京灵力检测有限公司",
    "code": "BJ_LINGLI",
    "address": "北京市朝阳区xxx",
    "contactPhone": "010-12345678",
    "contactEmail": "contact@bjlingli.com"
}

# 更新公司
PUT /api/v1/admin/companies/{companyId}

# 分配城市管理员
POST /api/v1/admin/companies/{companyId}/assign-admin
Content-Type: application/json

{
    "adminUserId": 1,
    "role": 2
}
```

### 6.3 套餐管理
```http
# 获取套餐列表
GET /api/v1/admin/packages?categoryId=1&status=1&page=1&size=10

# 创建套餐
POST /api/v1/admin/packages
Content-Type: application/json

{
    "categoryId": 1,
    "name": "高级体检套餐",
    "code": "PREMIUM_CHECKUP",
    "description": "包含全面检查项目",
    "price": 59900,
    "originalPrice": 79900,
    "coverImage": "https://cdn.example.com/premium.jpg",
    "testItems": ["血常规", "尿常规", "胸透", "心电图", "B超"],
    "samplingMethod": 3,
    "reportDeliveryDays": 5,
    "stock": 100
}

# 更新套餐
PUT /api/v1/admin/packages/{packageId}

# 上下架套餐
PUT /api/v1/admin/packages/{packageId}/status
Content-Type: application/json

{
    "status": 1
}
```

### 6.4 订单管理
```http
# 获取订单列表
GET /api/v1/admin/orders?status=1&companyId=1&startDate=2023-11-01&endDate=2023-11-30&page=1&size=10

# 订单详情
GET /api/v1/admin/orders/{orderId}

# 订单发货
POST /api/v1/admin/orders/{orderId}/ship
Content-Type: application/json

{
    "logisticsCompany": "顺丰快递",
    "trackingNumber": "SF1234567890",
    "remark": "已发货，请注意查收"
}

# 上传检验报告
POST /api/v1/admin/reports/upload
Content-Type: multipart/form-data

file: 上传的文件
orderId: 订单ID
orderItemId: 订单项ID
samplerId: 采样人ID
reportType: 报告类型
```

## 7. 文件上传接口

### 7.1 图片上传
```http
POST /api/v1/upload/image
Content-Type: multipart/form-data

file: 图片文件
module: 模块标识（package、user等）

Response:
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "fileName": "image_20231128_123456.jpg",
        "fileUrl": "https://cdn.example.com/images/image_20231128_123456.jpg",
        "fileSize": 102400,
        "width": 800,
        "height": 600
    }
}
```

### 7.2 报告文件上传
```http
POST /api/v1/upload/report
Content-Type: multipart/form-data

file: 报告文件（pdf/doc/docx）
orderId: 订单ID
orderItemId: 订单项ID
samplerId: 采样人ID
reportType: 报告类型

Response:
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "reportId": 1,
        "fileName": "检验报告_张三.pdf",
        "fileUrl": "https://cdn.example.com/reports/report_20231128_123456.pdf",
        "fileSize": 2048000
    }
}
```

## 8. 参数校验规则

### 8.1 用户相关
- username: 3-20字符，只能包含字母数字下划线
- password: 6-20字符，必须包含字母和数字
- phone: 11位手机号格式
- email: 标准邮箱格式
- idCard: 18位身份证号格式

### 8.2 订单相关
- quantity: 正整数，1-99
- amount: 正整数，单位：分
- address: 10-200字符
- contactName: 2-20字符
- contactPhone: 11位手机号格式

### 8.3 支付相关
- orderId: 正整数
- payType: 枚举值：WECHAT_PAY、ALIPAY

## 9. 限流策略

### 9.1 接口限流
- 登录接口：5次/分钟
- 注册接口：3次/分钟  
- 支付接口：10次/分钟
- 其他接口：100次/分钟

### 9.2 限流响应
```json
{
    "code": 429,
    "message": "请求过于频繁，请稍后再试",
    "data": null,
    "timestamp": 1635724800000
}
```

## 10. 性能优化

### 10.1 缓存策略
- 用户信息缓存：1小时
- 套餐信息缓存：30分钟
- 订单信息缓存：10分钟
- 分类信息缓存：1天

### 10.2 分页策略
- 默认每页10条记录
- 最大每页100条记录
- 大数据量查询使用游标分页

## 11. 监控告警

### 11.1 业务指标
- 接口成功率 > 99%
- 平均响应时间 < 500ms
- 订单支付成功率 > 95%

### 11.2 技术指标
- CPU使用率 < 80%
- 内存使用率 < 85%
- 数据库连接数 < 80%
- Redis命中率 > 95%