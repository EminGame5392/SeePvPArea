# SeePvPArea 🔥

[![Maven Version](https://img.shields.io/badge/version-1.2-blue)](https://github.com/EminGame5392/SeePvPArea/packages)
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

### Для разработчиков
Добавьте зависимость в ваш `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github-emingame5392</id>
        <name>GitHub EminGame5392 Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/EminGame5392/SeePvPArea</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ru.gortexdev</groupId>
        <artifactId>SeePvPArea</artifactId>
        <version>1.2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 🚀 Использование

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

## 🔌 API для разработчиков

### Получение API
```java
SeePvPAreaAPI api = Bukkit.getServicesManager().load(SeePvPAreaAPI.class);
if (api != null) {
    // API доступно
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
```

**Работа с системой задержек:**
```java
ItemDelayManager delayManager = api.getItemDelayManager();
boolean canUse = delayManager.canUseItem(player, Material.GOLDEN_APPLE);
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
        }
    }
    
    public void customCombatLogic(Player attacker, Player victim) {
        if (seepvpareaAPI != null) {
            seepvpareaAPI.setPlayersInCombat(attacker, victim);
        }
    }
}
```

### Доступные интерфейсы API

- `SeePvPAreaAPI` - основной интерфейс
- `CombatManager` - управление комбат-системой
- `ItemDelayManager` - управление задержками предметов

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
```
