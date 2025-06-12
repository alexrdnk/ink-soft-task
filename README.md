# Local News Project

A full-stack application for local news aggregation and analysis using Spring Boot and React.

## Features
- News aggregation from multiple sources
- AI-powered news classification
- Modern React frontend with Vite
- In-memory data storage
- Docker support

## Tech Stack
### Backend
- Java 17
- Spring Boot 3.5.0
- OpenAI API integration
- NewsAPI integration

### Frontend
- React
- Vite
- Modern CSS
- Axios for API calls

## Setup Instructions

### Prerequisites
- Java 17
- Node.js (LTS version)
- Docker (optional)

### Environment Setup

1. Backend Configuration:
   - Copy `src/main/resources/application-example.yml` to `src/main/resources/application-local.yml`
   - Update the values in `application-local.yml` with your actual credentials

2. Frontend Configuration:
   - Copy `localnews-frontend/.env.example` to `localnews-frontend/.env`
   - Update the environment variables with your actual values

3. Set up environment variables in IntelliJ IDEA:
   - Open Run/Debug Configurations
   - Add VM options: `-Dspring.profiles.active=local`
   - Add environment variables:
     - NEWSAPI_KEY=your_newsapi_key
     - OPENAI_API_KEY=your_openai_api_key

### Running the Application

#### Backend
1. Run the Spring Boot application with the 'local' profile
2. The backend will be available at `http://localhost:8080`

#### Frontend
1. Navigate to `localnews-frontend`
2. Install dependencies: `npm install`
3. Run development server: `npm run dev`
4. The frontend will be available at `http://localhost:5173`

### Docker Setup
```bash
# Build and run the backend (from project root)
./gradlew build
java -jar build/libs/*.jar

# Build and run the frontend (from localnews-frontend directory)
npm install
npm run build
```

## Development
- Backend API documentation is available at `http://localhost:8080/swagger-ui.html`
- Frontend development server includes hot reloading

## Security Notes
- Never commit `application-local.yml` or any files containing real API keys
- Use environment variables for sensitive data
- The example configuration files are safe to commit
- Keep your API keys secure and rotate them regularly

## Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details 