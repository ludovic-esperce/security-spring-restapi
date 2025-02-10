# Sécurisé une API Rest avec Spring Security et JWT

Le projet contenu dans ce dépôt va vous permettre de mettre en place des mécanismes de sécurité appliqués à une Web API

Afin de pouvoir faire fonctionner le projet il vous faut déployer une SGBD PostgreSQL.
Pour se faire, un fichier de configiration `docker-compose.yml` vous est founi.

Pour démarrer le conteneur Docker en question :
```
docker compose up
```

Une fois la base de données fonctionnelle vous pourrez commencer vos développements.

## Spring security

### Information de debug

Il est possible d'activer le mode debug en ajoutant une propriété à l'annotation suivante :
```java
@EnableWebSecurity(debug = true)
```

<p xmlns:cc="http://creativecommons.org/ns#" xmlns:dct="http://purl.org/dc/terms/"><span property="dct:title"> security-spring-restapi</span> by <a rel="cc:attributionURL dct:creator" property="cc:attributionName" href="https://github.com/afpa-learning">Afpa</a> is licensed under <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/?ref=chooser-v1" target="_blank" rel="license noopener noreferrer" style="display:inline-block;">CC BY-NC-SA 4.0<img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/cc.svg?ref=chooser-v1" alt=""><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/by.svg?ref=chooser-v1" alt=""><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/nc.svg?ref=chooser-v1" alt=""><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/sa.svg?ref=chooser-v1" alt=""></a></p> 