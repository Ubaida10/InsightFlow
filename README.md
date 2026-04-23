# InsightFlow

InsightFlow is a full-stack web application that provides data insights and analytics. It consists of a modern Angular frontend and a Spring Boot backend API.

## Project Structure

```
InsightFlow/
├── insight-flow-client/    # Angular frontend application
├── insight-flow-server/    # Spring Boot backend API
└── README.md              # This file
```

## Prerequisites

Before you begin, ensure you have the following installed:

### Global Requirements
- **Git** - for version control
- **Node.js** (v18+) - required for Angular development
- **npm** (v10+) - Node package manager
- **Java** (JDK 17+) - required for Spring Boot
- **Maven** (3.9+) - for building the Spring Boot project

### Verify Installations
```bash
# Check Node.js
node --version

# Check npm
npm --version

# Check Java
java -version

# Check Maven
mvn --version
```

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/InsightFlow.git
cd InsightFlow
```

### 2. Setup Backend (Spring Boot)
```bash
cd insight-flow-server
mvn clean install
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 3. Setup Frontend (Angular)
In a new terminal:
```bash
cd insight-flow-client
npm install
ng serve
```

The client will start on `http://localhost:4200`

### 4. Access the Application
Open your browser and navigate to `http://localhost:4200`

Click the "Test Server Connection" button to verify the client-server communication.

## Project Details

### Backend (insight-flow-server)
- **Framework**: Spring Boot 4.0.5
- **Java Version**: 17
- **Build Tool**: Maven
- **Port**: 8080

See [insight-flow-server/README.md](insight-flow-server/README.md) for detailed backend documentation.

### Frontend (insight-flow-client)
- **Framework**: Angular 21.2.0
- **TypeScript**: 5.9.2
- **Build Tool**: Angular CLI 21.2.8
- **Port**: 4200

See [insight-flow-client/README.md](insight-flow-client/README.md) for detailed frontend documentation.

## Environment Configuration

Both projects support environment-specific configuration:

### Client Environments
- **Development** (`src/environments/environment.ts`): Uses `http://localhost:8080`
- **Production** (`src/environments/environment.prod.ts`): Uses production API URL

### Server Environments
- **Default** (`application.properties`)
- **Development** (`application-dev.properties`)
- **Production** (`application-prod.properties`)

## Building for Production

### Backend
```bash
cd insight-flow-server
mvn clean package -DskipTests
# JAR file created at target/insight-flow-server-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd insight-flow-client
ng build --configuration production
# Built files in dist/insight-flow-client/
```

## Available Commands

### Backend (Spring Boot)
```bash
# Development build
mvn clean install

# Run development server
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Run tests
mvn test

# Create production build
mvn clean package
```

### Frontend (Angular)
```bash
# Install dependencies
npm install

# Start development server
ng serve

# Build for production
ng build --configuration production

# Run tests
ng test

# Run linting
ng lint
```

## API Documentation

### Base URL
- **Development**: `http://localhost:8080`
- **Production**: `https://api.insightflow.com` (update as needed)

### Endpoints

#### Health Check
- **GET** `/api/hello`
  - Returns: `"Hello from InsightFlow Server!"`
  - Used to verify server connectivity

For more API endpoints, see [insight-flow-server/README.md](insight-flow-server/README.md)

## CORS Configuration

The backend is configured to accept requests from the frontend on `http://localhost:4200` during development. This can be modified in the `HelloController.java` @CrossOrigin annotation for production environments.

## Troubleshooting

### Port Already in Use
If port 8080 (backend) or 4200 (frontend) is already in use:

**Backend**: Modify `application.properties`:
```properties
server.port=8081
```

**Frontend**: Run with different port:
```bash
ng serve --port 4201
```

### CORS Errors
Ensure the backend server is running and the frontend is configured with the correct API URL in `environment.ts`.

### Maven/NPM Dependency Issues
```bash
# Clear Maven cache
mvn clean

# Clear npm cache
npm cache clean --force
```

## Development Workflow

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make changes** in either frontend or backend

3. **Test locally** before committing

4. **Commit changes**
   ```bash
   git commit -m "feat: add your feature description"
   ```

5. **Push to repository**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a pull request**

## Contributing

See individual project README files for contribution guidelines:
- [Frontend Contributing](insight-flow-client/README.md#contributing)
- [Backend Contributing](insight-flow-server/README.md#contributing)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or suggestions, please:
1. Check existing issues on GitHub
2. Create a new issue with detailed description
3. Contact the development team

## Additional Resources

- [Angular Documentation](https://angular.dev)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
- [Maven Documentation](https://maven.apache.org/)

