# OCWALK

OCWALK is a fully free online application that allows you to learn, practice Ocarina and discover new songs and music to learn.

It features a project library, pre-filled with Ocarina pieces, four different practice modes and an additional "Watch" mode to preview the songs.

## Project links

Website: [ocwalk.com](http://ocwalk.com/)

Discord server: [join](https://discord.gg/FJ7r34W)

Backend/frontend code repo: [https://github.com/ocwalk/ocwalk](https://github.com/ocwalk/ocwalk)

Static UI repo: [https://github.com/ocwalk/ocwalk.github.io](https://github.com/ocwalk/ocwalk.github.io)

Kickstarter: [https://www.kickstarter.com/projects/owispyo/ocwalk](https://www.kickstarter.com/projects/owispyo/ocwalk)

## Useful commands

SBT:
```
ocwalkJS/run
ocwalkJVM/run
ocwalkJS/clean ocwalkJS/fastOptJS moveJS pushJS
ocwalkJVM/clean ocwalkJVM/assembly ocwalkJVM/docker
ocwalkJVM/clean ocwalkJVM/assembly ocwalkJVM/dockerBuildAndPush
```

Server:
```
-Dpac.bot.token=""
-Dpac.bot.server="poku club"
-Dpac.bot.channel="art_challenge"
-Dpac.processor.mongo="mongodb://"
-Dpac.processor.database=""
-Dpac.thumbnailer.awsAccess=""
-Dpac.thumbnailer.awsSecret=""
-Dpac.processor.retryImages="true"
```

Heroku:
```
heroku container:login
docker push registry.heroku.com/wispy-ocwalk/web
heroku container:release web --app wispy-ocwalk
heroku open --app wispy-ocwalk
heroku logs --tail --app wispy-ocwalk
```