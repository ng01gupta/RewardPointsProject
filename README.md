# Reward Points Calculator

## Project Overview
This application calculates reward points for customers based on their transactions over a three-month period.

## Design Details
- Customers earn:
  - 2 points for every dollar spent over $100.
  - 1 point for every dollar spent between $50 and $100.
- Example: $120 transaction = 2 x $20 + 1 x $50 = 90 points.

## Technical Details
- **Tech Stack**: Spring Boot, Java 17, Maven
- **Endpoints**:
  - `POST /calculate`: Accepts a list of transactions and returns reward points per month.

## Build and Run Instructions
1. Clone the repository: `git clone <repo_url>`
2. Build the project: `mvn clean install`
3. Run the application: `java -jar target/rewardpoints.jar`

## Testing
Run tests using: `mvn test`