# ReinforceLandPlugin

ReinforceLandPlugin is a Minecraft plugin developed for the **Reunification** modded server project on Minecraft version 1.12.2. It provides additional functionality for managing and reinforcing blocks within the game.

## Features

ReinforceLandPlugin offers the following features:

1. **Reinforcing Blocks:** Players can reinforce certain blocks with health points, making them harder to break.

2. **Block Health Display:** When a player punches a reinforced block (only available in reinforce mode), they can see its remaining health points as a holographic message.

3. **Block Ownership:** Reinforced blocks can be owned by players, and only the owner or trusted players can add more health to them.

**Note:** Trusted players also have the ability to add health to reinforced blocks if permitted.

## Installation

To install this plugin on your Minecraft server, follow these steps:

1. Download the latest plugin JAR file from the [Releases](https://github.com/arca-inc/ReinforceLandPlugin/releases) section.

2. Place the downloaded JAR file into the `plugins` folder of your Minecraft server directory.

3. Start or restart your Minecraft server.

4. Configure the plugin settings and permissions as needed (see Configuration below).

5. Enjoy the new features provided by ReinforceLandPlugin!

## Configuration

You can customize the plugin's behavior by editing the `config.yml` file located in the `plugins/ReinforceLandPlugin` folder. The configuration options include:

- **Health Values**: You can specify custom health values for different materials used to reinforce blocks.

- **Messages**: Customize messages sent to players when they reinforce blocks or interact with them.

- **Explosion Damage** : You can specify custom damage amounts per explosion.

- **Max Health** : You can specify the maximum health of reinforce blocks.

## Commands

ReinforceLandPlugin introduces the following command:

- `/reinforce`: Toggles reinforce mode for the player. While in this mode, players can reinforce blocks and see block health. Use the command again to disable this mode.
- `/reinforce trust <username>`: Trust a player. This command allows you to give another player permission to reinforce blocks on your land.
- `/reinforce trustlist`: View your trusted players. Use this command to see a list of players you've granted trust permissions to.
- `/reinforce untrust <username>`: Untrust a player. This command revokes a player's permission to reinforce blocks on your land.
- `/reinforce help`: Display the help message. This command shows a list of available commands and their descriptions.
- `/reinforce config`: Open GUI to control all configurations. This command opens a graphical user interface to configure plugin settings.

**Note**: Replace `<username>` with the actual username of the player you want to trust or untrust.

## Usage

Here's how players can use ReinforceLandPlugin:

1. To enter reinforce mode, a player can use the `/reinforce` command.

2. While in reinforce mode, players can reinforce blocks by right-clicking on them with specified items.

3. Players can check the health of a reinforced block by left-clicking on it.

4. Only the owner or relation player permitted of a reinforced block can add more health to it.

## Contributing

Contributions to the ReinforceLandPlugin project are welcome! If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.

## Issues

If you encounter any issues or have suggestions for improvements, please [create an issue](https://github.com/arca-inc/ReinforceLandPlugin/issues) on the GitHub repository.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

If you have any questions or need further assistance, you can join the [Reunification Discord server](https://discord.gg/EwDJkqbMXc) or contact the plugin developer, [Arca INC](https://github.com/arca-inc).

Enjoy using ReinforceLandPlugin on your Minecraft server!