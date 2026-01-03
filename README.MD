
# Discord Experiencebot - A discord bot
![Build](https://github.com/JFWenisch/discord-experiencebot/actions/workflows/build.yml/badge.svg) ![Version](https://img.shields.io/github/v/release/jfwenisch/discord-experiencebot) ![License](https://img.shields.io/github/license/jfwenisch/discord-experiencebot)   ![Maintainability](https://img.shields.io/codeclimate/maintainability/JFWenisch/discord-experiencebot)
 ![Size](https://img.shields.io/github/repo-size/jfwenisch/discord-experiencebot)  



A Java bot for [Discord](https://discordapp.com/) built with the [JDA library](https://github.com/DV8FromTheWorld/JDA). It awards experience (XP) to server members for time spent in voice channels and automatically assigns roles according to a logarithmic level system. Experience is persisted in a SQLite database.

<p>
<a href="https://discord.com/api/oauth2/authorize?client_id=959141855159779358&permissions=268435456&scope=bot" style="background-color:#5864FF;color:#ffffff;padding:10px 16px;border-radius:6px;text-decoration:none;font-weight:600;display:inline-block;">Add  to your server</a>
</p>
  


  
  

## Features
### XP system
 
The bot tracks the start and end time of every voice session: when a user joins a voice channel the session start is recorded and when they leave the session end is recorded. Each session's duration (end - start) is added to the user's accumulated `totalExp`. Users are awarded 1 XP for every second spent in voice (1 XP = 1 second). 
On session end the session duration is persisted to the database and the user is notified of the XP gained, their new total XP and current level.

### Ranking system

The auto ranking system is based on the time spent in voice channels per user per guild. The bot uses the onGuildVoiceJoin and onGuildVoiceLeave events to estimate the amount of time per sessions. For every second the user gets 1 XP which are persisted. On leave the users might get a private message (depending on personal discord settings concerning messaging) informing about collected XP, total XP and the current level.

The current Ranmlimg system is based on the logarithmic function `log(totalXP)/log(3)`.
This means that the following levels apply for the relating time.

| Level | Seconds | Minutes | Hours |
| ---: | ---: | ---: | ---: |
| 1 | 3 | 0.05 | 0.0008 |
| 2 | 9 | 0.15 | 0.0025 |
| 3 | 27 | 0.45 | 0.0075 |
| 4 | 81 | 1.35 | 0.0225 |
| 5 | 243 | 4.05 | 0.0675 |
| 6 | 729 | 12.15 | 0.2025 |
| 7 | 2,187 | 36.45 | 0.6075 |
| 8 | 6,561 | 109.35 | 1.8225 |
| 9 | 19,683 | 328.05 | 5.4675 |
| 10 | 59,049 | 984.15 | 16.4025 |
| 11 | 177,147 | 2,952.45 | 49.2075 |
| 12 | 531,441 | 8,857.35 | 147.6225 |
| 13 | 1,594,323 | 26,572.05 | 442.8675 |
| 14 | 4,782,969 | 79,716.15 | 1,328.6025 |
| 15 | 14,348,907 | 239,148.45 | 3,985.8075 |

  
### Automatic assignment of 'Regular' Role

If a member of the guild joins a voice channel join, the bot creates a new thread which lists all users that have been more than 3 times in voice and in total longer than 60 minutes within the last 14 days. These users are automatically added to a role name "%SERVERNAME& REGULARS". Guild members which are not (anymore) matching these filter are automatically removed from the role

  




## Host it yourself

### Installation 

#### Create discord application

 1. Go to https://discord.com/developers/applications and click on the `New Application` button
 2. Enter a Name for your application
 3. Navigate to `Bot` in the left sidebar and click on the `Add Bot` button
 4. Under `Privileged Gateway Intents` make sure to enable the `SERVER MEMBERS INTENT`  and `PRESENCE INTENT` (This is required for the bot to monitor joining & leaving members) and press the `Save Changes` button
 5. Click the `Reset Token` button and copy your token
 6. Navigate to  `OAuth2` in the left sidebar and click on `URL Generator` in the submenu of the sidebar
 7. Under `scopes` select `bot` and under `bot permissions` select `Manage Roles` and `Send Messages`
 8. Copy the generated url and open it in your browser to invite the bot

#### Deploy helm chart

Navigate to charts folder and run `helm upgrade -i discord-experiencebot --set discord.token=XXXXXX .`
## License

This project is licensed under the MIT License. See the `LICENSE` file for full terms.

## Contributing

Contributions are welcome — issues and pull requests are encouraged. If you find a bug or have a feature request, please open an issue at https://github.com/JFWenisch/discord-experiencebot/issues. To contribute code, fork the repository, create a branch for your changes, and open a pull request with a brief description and any relevant tests.
Thank you for helping improve the project!