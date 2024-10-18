# Telegram Bot Category Tree

This project is a Telegram bot for managing a category tree structure. It allows users to add, view, and remove categories in a hierarchical structure, and download the tree as an Excel file.

## Prerequisites

Before you can run the project, make sure you have the following installed:

- [Docker](https://docs.docker.com/get-started/get-docker/)

## How to Run

1. Clone the repository.
2. Set up your environment variables by creating a .env file in the root directory of the project
     ```
      BOT_TOKEN=your_telegram_bot_token_here
      DB_USERNAME=your_db_username
      DB_PASSWORD=your_db_password
      ```

3. Build and run the application using Docker Compose:
      ```
      docker compose up -d
      ```
