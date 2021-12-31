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
