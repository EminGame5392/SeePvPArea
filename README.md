# SeePvPArea 🔥

[![Minecraft](https://img.shields.io/badge/Minecraft-1.16.5%2B-green)](https://www.minecraft.net)
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://java.com)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

Профессиональный плагин для создания PvP арен с системой антирелога, проверками экипировки и расширенным API для разработчиков.

## ✨ Особенности

### 🎯 Основной функционал
- **Система PvP арен** с точками входа/выхода
- **Проверки экипировки** перед входом на арену
- **Система антирелога** для честных PvP боев
- **Кастомизируемые сообщения** (Title, BossBar, Chat)
- **Поддержка WorldGuard** регионов

### 🔧 Для разработчиков
- **Полноценное API** для интеграции с другими плагинами
- **Система задержек предметов** в бою
- **Управление комбат-режимом** через API
- **Поддержка сервисов** Bukkit/Spigot

## 📦 Установка

### Для сервера
1. Скачайте последнюю версию из [Releases](https://github.com/EminGame5392/SeePvPArea/releases)
2. Поместите файл `SeePvPArea.jar` в папку `plugins/`
3. Перезагрузите сервер

## 🔌 API для разработчиков

### 📥 Скачивание файлов API
Для использования API в своих плагинах скачайте из раздела [Releases](https://github.com/EminGame5392/SeePvPArea/releases):
- `SeePvPArea-1.2.jar` - основной JAR файл
- `SeePvPArea-1.2-sources.jar` - исходный код (опционально)
- `SeePvPArea-1.2-javadoc.jar` - документация (опционально)

### 🔧 Способ 1: Локальная установка в Maven (рекомендуется)

#### Установка JAR в локальный Maven репозиторий:
```bash
mvn install:install-file -Dfile=SeePvPArea-1.2.jar \
                         -DgroupId=ru.gortexdev \
                         -DartifactId=SeePvPArea \
                         -Dversion=1.2 \
                         -Dpackaging=jar \
                         -DgeneratePom=true
```

#### Добавление зависимости в ваш проект:
**pom.xml:**
```xml
<dependencies>
    <dependency>
        <groupId>ru.gortexdev</groupId>
        <artifactId>SeePvPArea</artifactId>
        <version>1.2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 📁 Способ 2: Включение JAR в проект через system scope

#### 1. Создайте папку `lib` в корне вашего проекта
#### 2. Поместите `SeePvPArea-1.2.jar` в папку `lib/`
#### 3. Настройте зависимость:
**pom.xml:**
```xml
<dependencies>
    <dependency>
        <groupId>ru.gortexdev</groupId>
        <artifactId>SeePvPArea</artifactId>
        <version>1.2</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/SeePvPArea-1.2.jar</systemPath>
    </dependency>
</dependencies>
```

### 🗂️ Способ 3: Ручное копирование в ресурсы

#### 1. Создайте структуру папок:
```
src/main/resources/libs/
└── SeePvPArea-1.2.jar
```

#### 2. Настройте плагин для загрузки JAR из ресурсов
**В основном классе вашего плагина:**
```java
@Override
public void onEnable() {
    // Копирование JAR из ресурсов во временную папку
    saveResource("libs/SeePvPArea-1.2.jar", false);
    
    // Динамическая загрузка классов (продвинутый способ)
    loadExternalJar(new File(getDataFolder(), "SeePvPArea-1.2.jar"));
}
```

## 🚀 Использование API

### Получение API
```java
SeePvPAreaAPI api = Bukkit.getServicesManager().load(SeePvPAreaAPI.class);
if (api != null) {
    getLogger().info("SeePvPArea API successfully loaded!");
} else {
    getLogger().warning("SeePvPArea not found! Make sure it's installed on the server.");
}
```

### Примеры использования

**Установка комбата между игроками:**
```java
api.setPlayersInCombat(player1, player2);
```

**Проверка статуса комбата:**
```java
boolean inCombat = api.isInCombat(player);
if (inCombat) {
    player.sendMessage("Вы в режиме боя!");
}
```

**Работа с системой задержек:**
```java
ItemDelayManager delayManager = api.getItemDelayManager();
if (delayManager.canUseItem(player, Material.GOLDEN_APPLE)) {
    // Разрешить использование
    delayManager.setItemUseTime(player, Material.GOLDEN_APPLE);
} else {
    long delay = delayManager.getItemUseDelay(player, Material.GOLDEN_APPLE);
    player.sendMessage("Подождите " + delay + " секунд");
}
```

**Полная интеграция:**
```java
public class YourPlugin extends JavaPlugin {
    private SeePvPAreaAPI seepvpareaAPI;
    
    @Override
    public void onEnable() {
        seepvpareaAPI = getServer().getServicesManager().load(SeePvPAreaAPI.class);
        if (seepvpareaAPI != null) {
            getLogger().info("SeePvPArea API successfully loaded!");
            registerEvents();
        }
    }
    
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerDamage(EntityDamageByEntityEvent event) {
                if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                    Player victim = (Player) event.getEntity();
                    Player attacker = (Player) event.getDamager();
                    seepvpareaAPI.setPlayersInCombat(attacker, victim);
                }
            }
        }, this);
    }
}
```

### Доступные интерфейсы API

- `SeePvPAreaAPI` - основной интерфейс
- `CombatManager` - управление комбат-системой
- `ItemDelayManager` - управление задержками предметов

## ⚙️ Конфигурация

Основные настройки в `config.yml`:

```yaml
settings:
  entrance:
    checks:
      armor_check:
        enable: true
        error-message: "&cНаденьте полный комплект брони!"
      hp_check:
        enable: true
        minimal_hp: 14
      weapon_check:
        enable: true
  anti_relog:
    duration: 30
    delays:
      golden_apple: 10
      ender_pearl: 10
```

[Полный пример config.yml](config.yml)

## 🎮 Использование плагина

### Основные команды
| Команда | Описание | Права |
|---------|-----------|--------|
| `/pvpent` | Вход на PvP арену | `seepvparea.use` |
| `/pvpext` | Выход с PvP арены | `seepvparea.use` |
| `/seepvparea set <pvpent\|pvpext>` | Установка точек | `seepvparea.admin` |
| `/seepvparea reload` | Перезагрузка конфигурации | `seepvparea.admin` |

### Настройка арены
1. Установите точку входа: `/seepvparea set pvpent`
2. Установите точку выхода: `/seepvparea set pvpext`
3. Настройте проверки в `config.yml`

## 🛠️ Разработка

### Сборка из исходников
```bash
git clone https://github.com/EminGame5392/SeePvPArea.git
cd SeePvPArea
mvn clean package
```

### Структура проекта
```
src/
├── main/
│   ├── java/ru/gortexdev/seepvparea/
│   │   ├── api/          # Публичное API
│   │   ├── commands/     # Команды плагина
│   │   └── utils/        # Вспомогательные классы
│   └── resources/
│       ├── plugin.yml    # Конфигурация плагина
│       └── config.yml    # Файл настроек
```

## 📋 Требования

- **Minecraft Server**: 1.16.5+
- **Java**: 8+
- **Bukkit/Spigot**: Совместимые версии

## ❓ Часто задаваемые вопросы

**Q: Почему нельзя использовать Maven репозиторий GitHub Packages?**  
A: В текущей версии возникли проблемы с аутентификацией при публикации пакетов. Используйте локальную установку через `mvn install:install-file`.

**Q: Как проверить, что API загружено правильно?**  
A: Используйте код проверки из примеров выше. Если API возвращает `null`, убедитесь что SeePvPArea установлен на сервере.

**Q: Можно ли использовать API без установки плагина на сервер?**  
A: Нет, плагин SeePvPArea должен быть установлен на сервере для работы API.

## 🐛 Баг-репорты и фичи

Нашли баг или есть предложение? Создайте [Issue](https://github.com/EminGame5392/SeePvPArea/issues) с подробным описанием.

## 📄 Лицензия

Этот проект лицензирован под MIT License - смотрите файл [LICENSE](LICENSE) для деталей.

## 👥 Автор

**EminGame5392**
- GitHub: [@EminGame5392](https://github.com/EminGame5392)
- Websites: [seemine.su](https://seemine.su), [gdev.pro](https://gdev.pro)

## 🤝 Вклад в проект

Вклады приветствуются! Не стесняйтесь форкать проект и создавать pull requests.

---

**⭐ Не забудьте поставить звезду, если плагин вам понравился!**
