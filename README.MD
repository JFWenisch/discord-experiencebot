
# Discord Experiencebot - A discord bot

  

A Java bot for [Discord](https://discordapp.com/) using the [JDA library](https://github.com/DV8FromTheWorld/JDA).

Current functionality is about giving members of a discord server EXP when being in a voice channel and automatically assigning roles based on a level system. This bot uses an postgreSQL instance to persist experience. If you'd like to run your own version of this project check the [installation part]

  
  

## Ranking system

The auto ranking system is based on the time spent in voice channels per user per guild. The bot uses the onGuildVoiceJoin and onGuildVoiceLeave events to estimate the amount of time per sessions. For every second the user gets 1 XP which are persisted. On leave the user gets a private message informing about collected XP, total XP and the current level.

  

*note: In order for this to work the bot needs the manage roles permission on discord.*

  

The current ranking system is based on logarithmic function:

  
  

```

log(totalXP)/log(3)

```

  
  

XP | LVL |

--- | --- |

150 | 4

3739 | 7

15160 | 8

20762 | 9

156089 | 10

556089 | 12

  

## Automatic assignment of 'REGULARS'

Upon voice join, the bot creates a new thread which lists all users that have been more than 3 times in voice and in total longer than 60 minutes within the last 14 days. These users are automatically added to a role name "%SERVERNAME& REGULARS". Guild members which are not (anymore) matching these filter are automatically removed from the role

  

## Installation [IN PROGRESS]

## Creating your personal bot

 1. Go to https://discord.com/developers/applications and click on the `New Application` button
 2. Enter a Name for your application
 3. Navigate to `Bot` in the left sidebar and click on the `Add Bot` button
 4. Under `Privileged Gateway Intents` make sure to enable the `SERVER MEMBERS INTENT`  and `PRESENCE INTENT` (This is required for the bot to monitor joining & leaving members) and press the `Save Changes` button
 5. Click the `Reset Token` button and copy your token
 6. Navigate to  `OAuth2` in the left sidebar and click on `URL Generator` in the submenu of the sidebar
 7. Under `scopes` select `bot` and under `bot permissions` select `Manage Roles` and `Send Messages`
 8. Copy the generated url and open it in your browser to invite the bot

## Deploying your bot using helm chart

Navigate to charts folder and run `helm upgrade -i discord-experiencebot --set discord.token=XXXXXX .`