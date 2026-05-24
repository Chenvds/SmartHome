# SmartHome - 全屋智能家居系统

一个完整的全屋智能家居控制面板，支持设备查看、状态监控与远程控制。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Vue Router 4 + Pinia |
| 后端 | Spring Boot 3.2 + Java 17 |
| 部署 | Docker Compose (Nginx + Spring Boot) |

## 快速启动

### 方式一：本地开发

依赖：Java 17+、Maven 3.9+、Node.js 20+

```bash
# 终端 1 — 后端
cd back
mvn spring-boot:run

# 终端 2 — 前端
cd front
npm install
npm run dev
```

访问 http://localhost:5173

### 方式二：一键启动（本地）

```bash
# Windows
start.bat

# macOS / Linux
chmod +x start.sh
./start.sh
```

### 方式三：Docker 部署

```bash
# 先构建前端
cd front && npm install && npm run build && cd ..

# 启动全部服务
docker compose up -d
```

访问 http://localhost:8080

## 项目结构

```
project/smarthome/
├── front/                     # Vue 3 前端
│   ├── src/
│   │   ├── api/              # API 请求封装
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── components/       # 页面组件
│   │   └── assets/           # 样式与资源
│   └── .env.development      # 开发环境变量
│
├── back/                      # Spring Boot 后端
│   ├── src/main/java/com/smarthome/
│   │   ├── controller/       # REST API
│   │   ├── service/          # 业务逻辑
│   │   ├── model/            # 数据模型
│   │   ├── dto/              # 数据传输对象
│   │   ├── exception/        # 异常处理
│   │   └── config/           # 配置
│   └── Dockerfile            # 后端容器化
│
├── docker-compose.yml        # 全栈编排
├── nginx.conf                # Nginx 配置
├── start.bat / start.sh      # 启动脚本
└── .gitignore
```

## API 文档

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/rooms` | 获取所有房间及设备 |
| GET | `/api/v1/devices` | 获取所有设备列表 |
| GET | `/api/v1/devices/{id}` | 获取单个设备 |
| POST | `/api/v1/devices/{id}/toggle` | 开关设备 |
| PUT | `/api/v1/devices/{id}` | 更新设备属性 |
| GET | `/actuator/health` | 健康检查 |
