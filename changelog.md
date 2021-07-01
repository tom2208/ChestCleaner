# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [2.4.0] 
### Added
- Added subcommands to change sorting sound: `/sortingadmin sortingSound set <sound> <volume> <pitch>`
- Added cooldown to `/cleaningitem get` command

### Changed
- Refactored the whole command structure: A new tree command structure got implemented

### Fixed
- Fixed bug which caused older Minecraft versions to produce an error on loading
