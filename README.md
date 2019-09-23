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
all/clean: ocwalkJVM/clean ocwalkJS/clean
jvm/build: ocwalkJVM/clean ocwalkJVM/compile ocwalkJVM/assembly
js/build:  ocwalkJS/clean  ocwalkJS/compile  ocwalkJS/fastOptJS nodeJS moveJS
js/deploy: ocwalkJS/clean  ocwalkJS/compile  ocwalkJS/fullOptJS nodeJS moveJS pushJS
```

Server:
```
```

Heroku:
```
heroku container:login
docker push registry.heroku.com/wispy-ocwalk/web
heroku container:release web --app wispy-ocwalk
heroku open --app wispy-ocwalk
heroku logs --tail --app wispy-ocwalk
```