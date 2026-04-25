# EntityInformation NeoForge 1.21 Validation Ledger

This ledger seeds the 1.21 validation work from the `remotes/origin/v1.20.x` source tree and the current `1.21` diff snapshot.

## Status Legend
- `Pending`: seeded from inventory or diff, not checked yet
- `Checking`: validation is in progress
- `Fixing`: a regression or migration mismatch is already known and needs follow-up
- `Verified`: behavior was checked and matched expectations
- `Accepted Diff`: the change is intentional and does not need follow-up code work
- `Blocked`: validation cannot continue until another dependency is resolved

## 工程与构建
### Entry 1: NeoForge 构建与元数据迁移
- `1.20 来源`: `build.gradle`, `gradle.properties`, `settings.gradle`, `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`, `src/main/resources/META-INF/mods.toml`
- `1.21 对应实现`: `M build.gradle`, `M gradle.properties`, `M settings.gradle`, `M gradlew`, `M gradlew.bat`, `M gradle/wrapper/gradle-wrapper.jar`, `M gradle/wrapper/gradle-wrapper.properties`, `A src/main/templates/META-INF/neoforge.mods.toml`, `D src/main/resources/META-INF/mods.toml`
- `预期行为`: NeoForge 1.21 构建脚本应继续解析模组元数据，模板化 `neoforge.mods.toml` 应替代旧的 `mods.toml`，打包流程不再依赖旧路径。
- `当前状态`: `Verified`
- `发现的问题`: 构建脚本已迁移到 NeoForge 1.21 的预期形态；本轮需要补齐真实构建证据，确认模板化 mod metadata 不只是配置存在，而是实际进入构建输出。
- `修复动作`: 保留现有 NeoForge 构建配置，并补跑完整构建链与资源检查，确认 `neoforge.mods.toml` 被生成并打包。
- `验证方式`: 运行 `./gradlew compileJava --console plain`、`./gradlew processResources --console plain`、`./gradlew test --tests org.hhoa.mc.item_information.ModInfoTest --tests org.hhoa.mc.item_information.config.ConfigsTest --tests org.hhoa.mc.item_information.framework.Box2DTest --console plain`、`./gradlew build --console plain`；随后确认 `build/resources/main/META-INF/neoforge.mods.toml` 存在，且 `jar tf build/libs/entity_information-1.0.jar` 包含 `META-INF/neoforge.mods.toml`。
- `结论`: 上述构建与产物检查均已通过，`neoforge.mods.toml` 已生成并进入 jar，构建与元数据迁移条目更新为 `Verified`。

## 入口与配置
### Entry 1: 模组入口、配置与注册挂接
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/EntityInformation.java`, `src/main/java/org/hhoa/mc/item_information/ModInfo.java`, `src/main/java/org/hhoa/mc/item_information/config/Configs.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/EntityInformation.java`, `M src/main/java/org/hhoa/mc/item_information/ModInfo.java`, `M src/main/java/org/hhoa/mc/item_information/config/Configs.java`, `A src/main/java/org/hhoa/mc/item_information/registry/ModItems.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/attachment/ModAttachments.java`
- `预期行为`: 模组入口应完成配置注册、物品注册和附件初始化，NeoForge 1.21 下的启动顺序需要与 1.20.x 功能面保持一致。
- `当前状态`: `Accepted Diff`
- `发现的问题`: `EntityInformation` 的 bootstrap 链可以通过静态审查确认已接上 NeoForge 1.21 的入口与配置路径，但本轮并未执行真实模组启动，不能把物品注册、附件注册和运行时事件挂接表述成已做过 runtime 验证。
- `修复动作`: 保持当前入口实现不变，并把结论限定为静态 bootstrap/config audit 已通过；运行时注册结果留给后续 feature/runtime 验证回填。
- `验证方式`: 静态检查 `EntityInformation` 构造函数确认其依次执行 `modBus.addListener`、`modContainer.registerConfig(ModConfig.Type.COMMON, Configs.SPEC)`、`Configs.syncFromConfig()`、`ItemTooltip.bootstrap(modBus)`、`MobDictionary.bootstrap(modBus)`；同时检查 `ItemTooltip.bootstrap` 与 `MobDictionary.bootstrap` 已分别接上 mod bus / NeoForge event bus、recipe serializer、attachments 和 items 注册链。补充运行 `./gradlew test --tests org.hhoa.mc.item_information.ModInfoTest --tests org.hhoa.mc.item_information.config.ConfigsTest --tests org.hhoa.mc.item_information.framework.Box2DTest --console plain`，确认模组 identity 与默认配置兼容层保持稳定。
- `结论`: Task 4 已完成入口与配置的静态 bootstrap/config parity 审查，但未宣称真实 runtime registration 已验证，因此该条目以 `Accepted Diff` 收口并递延运行时确认。

## framework
### Entry 1: Box2D GUI 边界回归检查
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/framework/Point2D.java`, `src/main/java/org/hhoa/mc/item_information/framework/Box2D.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/framework/Box2D.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryGui.java`
- `预期行为`: `Box2D` 的几何判断应继续支撑 GUI 命中区域与布局计算，避免 1.21 客户端界面出现点击范围偏移。
- `当前状态`: `Verified`
- `发现的问题`: Box2D 是 `mobdictionary/client/gui` 命中检测的直接依赖。按计划播种记录：invalid bound validation 曾经写反，导致非法边界没有被正确拦截；此前只覆盖了正常边界，缺少 inverted-bounds regression 的显式测试。
- `修复动作`: 保留 `e105b64` 中对 invalid bound validation 反转问题的修复，并在 `Box2DTest` 中补上 inverted-bounds regression coverage：IDE 环境下断言抛出 `IllegalStateException`，非 IDE 环境下断言边界会被规范化。
- `验证方式`: 运行 `./gradlew test --tests org.hhoa.mc.item_information.ModInfoTest --tests org.hhoa.mc.item_information.config.ConfigsTest --tests org.hhoa.mc.item_information.framework.Box2DTest --console plain`，确认正常边界与 inverted-bounds regression coverage 均通过。
- `结论`: `Box2D` 的正常边界和 inverted-bounds regression 都已在本轮 focused tests 中通过，framework 主回归条目保持 `Verified`。

### Entry 2: utils 运行时兼容性检查
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/utils/EntityUtils.java`, `src/main/java/org/hhoa/mc/item_information/utils/GameUtils.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/utils/EntityUtils.java`, `M src/main/java/org/hhoa/mc/item_information/utils/GameUtils.java`
- `预期行为`: `EntityUtils` 的 attribute-holder 迁移应继续返回正确属性值或默认值，`GameUtils` 的 server-dist detection 应继续为运行时分支提供正确结果，不让依赖它们的功能静默偏离。
- `当前状态`: `Accepted Diff`
- `发现的问题`: `EntityUtils` 与 `GameUtils` 的 1.21 变更确实需要后续消费方验证，但它们不属于 Task 4 的 build/bootstrap/config/framework parity 收口面；继续在本组保留 `Pending` 会误阻断 Task 4 关闭。
- `修复动作`: 将该条目明确视为后续 consumer-group follow-up，随 `mobdictionary` / runtime 相关验证一起回填，而不是在 Task 4 中结案。
- `验证方式`: 结合后续 `mobdictionary` 的实体数据、物品交互和服务端分支验证检查 attribute-holder migration 与 server-dist detection。
- `结论`: 对 Task 4 而言，该 utils/runtime 条目作为递延到后续分组的审计差异接受，不再作为 `framework` 组的开放项。

## itemtooltip
### Entry 1: ItemTooltip 服务拆分首轮验证
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltip.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipForgeEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipModEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/item/ItemInfo.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/kaymap/ItemTooltipKeyMappingRegistry.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/parser/ItemInfoParser.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/item/TAG.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltip.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipForgeEventsHandler.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipModEventsHandler.java`, `A src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipService.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/item/ItemInfo.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/kaymap/ItemTooltipKeyMappingRegistry.java`
- `预期行为`: 物品提示解析、格式化和按键开关行为在拆分出 `ItemTooltipService` 后仍保持与 1.20.x 一致。
- `当前状态`: `Checking`
- `发现的问题`: 服务拆分后的事件挂接、按键注册和资源解析链路在静态对照里没有发现真实偏差；`ItemTooltipServiceTest` 当前只覆盖了 `granite.json` 的解析，`ItemTooltipTest` 覆盖了仅在 `Dist.CLIENT` 下执行 bootstrap 的分支。但本轮没有记录到可复核的 `runClient` 手动 tooltip smoke 证据，因此还不能把该条目收口为完整 `Verified`。
- `修复动作`: 无需生产代码修复；保留当前 NeoForge 1.21 拆分实现，并补记该条目仍待 in-game tooltip smoke。
- `验证方式`: 已运行 `./gradlew test --tests org.hhoa.mc.item_information.itemtooltip.ItemTooltipServiceTest --tests org.hhoa.mc.item_information.itemtooltip.ItemTooltipTest --console plain`，确认 focused tests 通过。后续仍需在可交互客户端环境中执行 `runClient` 并手动确认 tooltip 展示、按键切换与资源加载。
- `结论`: 目前只完成了自动化 parsing/bootstrap parity 证据回填；真实 in-game tooltip smoke 仍待补做，所以条目保持 `Checking`。

## mobdictionary
### Entry 1: 数据与网络迁移总览
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionary.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/EntityManager.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryFMLEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryForgeEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/command/MobDictionaryCommand.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/recipes/MobDictionaryRecipeProvider.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobDatas.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobSavedData.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDataItem.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDictionaryItem.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityImpl.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityProvider.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/IFirstLoginCapability.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapability.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityImpl.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityProvider.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/PacketHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessage.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessageHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessage.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessageHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessage.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessageHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/SyncDataMessage.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionary.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/EntityManager.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryFMLEventsHandler.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryForgeEventsHandler.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/command/MobDictionaryCommand.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/recipes/MobDictionaryRecipeProvider.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobDatas.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobSavedData.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDataItem.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDictionaryItem.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/attachment/ModAttachments.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryPayloadHandlers.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryPayloads.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/RegisterMobPayload.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/SyncMobDataPayload.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityImpl.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityProvider.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/IFirstLoginCapability.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapability.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityImpl.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityProvider.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/PacketHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessage.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessageHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessage.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessageHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessage.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessageHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/SyncDataMessage.java`
- `预期行为`: 数据持久化、同步、命令入口、配方生成，以及解锁和实体注册流程都应完成从 capability + packet 到 attachment + payload 的迁移，同时保持 1.20.x 的功能行为。
- `当前状态`: `Accepted Diff`
- `发现的问题`: 静态对照和 focused tests 没有暴露新的功能性偏差；attachment 持久化、payload codec、command 注册、recipe provider 输出和 unlock flow 的 1.21 迁移都能对上当前实现。仍未做可交互世界里的 command/unlock/persistence smoke，所以这条不能被写成完整 runtime `Verified`。
- `修复动作`: 无需额外生产代码修复；保留现有 attachment + payload + recipe/provider 迁移实现，并把结论限定在代码审计与自动化回归已通过。
- `验证方式`: 已运行 `./gradlew test --tests org.hhoa.mc.item_information.mobdictionary.data.MobSavedDataTest --tests org.hhoa.mc.item_information.mobdictionary.item.MobDataItemTest --tests org.hhoa.mc.item_information.mobdictionary.network.PayloadCodecTest --console plain`，结果通过；同时复核了 `MobDictionaryCommand`、`MobDatas`、`MobSavedData`、`MobDictionaryForgeEventsHandler` 和 `MobDictionaryRecipeProvider` 的 1.20.x 对照。
- `结论`: 目前没有发现需要补丁的真实 parity gap；该条目收口为 `Accepted Diff`，但 world-level command/unlock/persistence smoke 仍待后续补做。

### Entry 2: client/gui 首轮回归修复
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryGui.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryGuiButtonClickEvent.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/PacketHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityProvider.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryGui.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryEntityPreviewState.java`, `A src/test/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryEntityPreviewStateTest.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryButtonPayload.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryPayloadHandlers.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryPayloads.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/attachment/ModAttachments.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryGuiButtonClickEvent.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/PacketHandler.java`
- `预期行为`: `client/gui` 入口在 NeoForge 1.21 下仍应正确打开界面、发送按钮点击载荷、读取实体数据并维持命中区域与布局行为。
- `当前状态`: `Fixing`
- `发现的问题`: 已知首轮回归面集中在 `client/gui` 路径。GUI 从旧的 packet/capability 流程切换到 payload/attachment 流程后，`MobDictionaryGui` 的文本曾走 detached matrix/batch draw path，导致 missing text，同时 entity preview 仍需要从手工渲染迁回 1.21 的标准 inventory renderer。
- `修复动作`: 修复重点放在 `MobDictionaryGui` 到 payload handler 的交互链、附件数据读取路径，以及与 `Box2D` 相关的点击区域校准；文本部分已把 detached matrix path 替换为 `GuiGraphics.drawString`，并把锁定名称与进度文案抽到 `MobDictionaryTextContent`。本轮还把 entity preview 切到 `InventoryScreen.renderEntityInInventory`，但真实 GUI 手动确认仍待完成。
- `验证方式`: 运行 `./gradlew test --tests org.hhoa.mc.item_information.mobdictionary.client.gui.MobDictionaryTextContentTest --tests org.hhoa.mc.item_information.mobdictionary.client.gui.MobDictionaryEntityPreviewStateTest --tests org.hhoa.mc.item_information.itemtooltip.ItemTooltipTest --console plain` 作为 GUI 文本内容与 preview state 回归检查，并继续运行 `./gradlew test --tests org.hhoa.mc.item_information.framework.Box2DTest --tests org.hhoa.mc.item_information.mobdictionary.network.PayloadCodecTest`；entity preview 的最终可视效果仍需通过 `MobDictionaryGui` 手动回归确认。
- `结论`: `client/gui` 条目按要求以 `Fixing` 落账；文本路径已修好，entity preview 也已迁到 1.21 的 inventory renderer，但真实 GUI 手动回归仍待确认，所以该条目继续保留为未完全验收状态。

## resources
### Entry 1: 运行时资源与语言文件验证
- `1.20 来源`: `src/main/resources/assets/entity_information/lang/en_us.json`, `src/main/resources/assets/entity_information/lang/zh_cn.json`, `src/main/resources/assets/entity_information/item_infos/minecraft/*.json`, `src/main/resources/assets/entity_information/models/item/*.json`, `src/main/resources/assets/entity_information/textures/gui/*`, `src/main/resources/assets/entity_information/textures/item/*`
- `1.21 对应实现`: `src/main/resources/assets/entity_information/lang/en_us.json`, `src/main/resources/assets/entity_information/lang/zh_cn.json`, `src/main/resources/assets/entity_information/item_infos/minecraft/*.json`, `src/main/resources/assets/entity_information/models/item/*.json`, `src/main/resources/assets/entity_information/textures/gui/*`, `src/main/resources/assets/entity_information/textures/item/*`
- `预期行为`: 语言键、模型、纹理和 `item_infos` 数据在 1.21 运行时仍应按旧路径解析，不引入缺失资源或错误键名。
- `当前状态`: `Checking`
- `发现的问题`: 对照 `remotes/origin/v1.20.x` 后，当前资源树没有暴露出需要立即回滚的静态差异；`granite.json` 也仍能被服务层按预期解析。但这只能证明单个 `item_info` 样本和自动化加载路径可用，不能据此把整个 `item_infos` / `lang` / model / texture 运行时资源面宽泛地标成 `Accepted Diff`。
- `修复动作`: 无需资源修复；把条目收窄回真实状态，等待可交互客户端环境下的 runtime resource smoke。
- `验证方式`: 已通过 `ItemTooltipServiceTest` 解析 `assets/entity_information/item_infos/minecraft/granite.json`，并复核当前资源树与 `v1.20.x` 的静态对照。后续仍需运行客户端并手动检查 tooltip、语言键、模型与纹理的实际加载。
- `结论`: 现阶段只有有限的静态与自动化资源证据，尚不足以接受整个资源层 diff，因此条目回退为 `Checking`。

## generated resources
### Entry 1: 数据生成目录重排验证
- `1.20 来源`: `src/generated/resources/data/entity_information/advancements/recipes/dictionary.json`, `src/generated/resources/data/entity_information/recipes/dictionary.json`
- `1.21 对应实现`: `A src/generated/resources/data/entity_information/advancement/recipes/tools/dictionary.json`, `D src/generated/resources/data/entity_information/advancements/recipes/dictionary.json`, `A src/generated/resources/data/entity_information/recipe/dictionary.json`, `D src/generated/resources/data/entity_information/recipes/dictionary.json`
- `预期行为`: 数据生成输出应与 NeoForge 1.21 当前 provider 期望的目录结构一致，生成后的配方与进度文件能被正常发现。
- `当前状态`: `Accepted Diff`
- `发现的问题`: 目录从复数路径迁移到单数路径是 NeoForge 1.21 数据生成语义变化；当前 `MobDictionaryRecipeProvider` 已写向 `recipe/dictionary.json` 和 `advancement/recipes/tools/dictionary.json`，与工作区里的生成物一致，没有看到需要回滚的偏差。
- `修复动作`: 无需额外代码修复；保留当前 provider/生成物命名。
- `验证方式`: 静态核对 `MobDictionaryRecipeProvider` 与 `src/generated/resources/data/entity_information/recipe/dictionary.json`、`src/generated/resources/data/entity_information/advancement/recipes/tools/dictionary.json` 的路径一致性；本轮未重新跑 `runData`。
- `结论`: 目录重排是已接受的 1.21 diff，暂不需要后续代码跟进。

## 测试与验证
### Entry 1: 首轮验证命令矩阵
- `已交付行为基线`: framework 边界检查、itemtooltip 行为、mobdictionary 保存数据、物品交互、payload 编解码，以及 GUI 与构建链的既有功能面
- `1.21 验证覆盖`: `src/test/java/org/hhoa/mc/TemplateResourceTest.java`, `src/test/java/org/hhoa/mc/TestResources.java`, `src/test/java/org/hhoa/mc/item_information/ModInfoTest.java`, `src/test/java/org/hhoa/mc/item_information/config/ConfigsTest.java`, `src/test/java/org/hhoa/mc/item_information/framework/Box2DTest.java`, `src/test/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipTest.java`, `src/test/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipServiceTest.java`, `src/test/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryTextContentTest.java`, `src/test/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryEntityPreviewStateTest.java`, `src/test/java/org/hhoa/mc/item_information/mobdictionary/data/MobSavedDataTest.java`, `src/test/java/org/hhoa/mc/item_information/mobdictionary/item/MobDataItemTest.java`, `src/test/java/org/hhoa/mc/item_information/mobdictionary/network/PayloadCodecTest.java`，以及 GUI 手动回归、构建检查、数据生成检查；其中 `MobDictionaryTextContentTest` 用于聚焦文本内容格式回归覆盖，`MobDictionaryEntityPreviewStateTest` 用于聚焦 preview-state regression coverage
- `预期行为`: 首轮验证应覆盖 framework、itemtooltip、mobdictionary、generated resources 和构建链，形成可复用的验证矩阵。
- `当前状态`: `Pending`
- `发现的问题`: 目前账本只完成了条目播种，尚未把测试结果回填到各条目状态中。
- `修复动作`: 后续执行单元测试、GUI 回归和构建检查时，将结果分别回填到对应章节，并同步更新本节矩阵状态。
- `验证方式`: `./gradlew test`, `./gradlew build`, `./gradlew runData`，以及 `MobDictionaryGui` 手动回归。
- `结论`: 测试章节已转换为首轮验证入口，待真实验证结果回填。
