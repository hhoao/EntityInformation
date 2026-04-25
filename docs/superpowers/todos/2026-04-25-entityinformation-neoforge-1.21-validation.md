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
- `当前状态`: `Pending`
- `发现的问题`: 当前只完成了源树和 diff 编目，尚未确认模板展开、资源打包和 Gradle 属性迁移是否完全一致。
- `修复动作`: 首轮验证时检查 `processResources`、模组元数据注入和发行物内容；若仍引用旧 `mods.toml`，补齐构建脚本或模板参数。
- `验证方式`: 先运行 `./gradlew --version` 确认 wrapper 与 Gradle 版本正常，再运行 `./gradlew compileJava`、`./gradlew test`、`./gradlew build` 与 `./gradlew processResources`，并检查生成产物中的 NeoForge 模组元数据文件。
- `结论`: 作为首轮验证入口保留，待构建检查后再更新为 `Verified` 或 `Accepted Diff`。

## 入口与配置
### Entry 1: 模组入口、配置与注册挂接
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/EntityInformation.java`, `src/main/java/org/hhoa/mc/item_information/ModInfo.java`, `src/main/java/org/hhoa/mc/item_information/config/Configs.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/EntityInformation.java`, `M src/main/java/org/hhoa/mc/item_information/ModInfo.java`, `M src/main/java/org/hhoa/mc/item_information/config/Configs.java`, `A src/main/java/org/hhoa/mc/item_information/registry/ModItems.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/attachment/ModAttachments.java`
- `预期行为`: 模组入口应完成配置注册、物品注册和附件初始化，NeoForge 1.21 下的启动顺序需要与 1.20.x 功能面保持一致。
- `当前状态`: `Pending`
- `发现的问题`: 入口代码已经迁移到新的物品注册和附件挂接路径，但尚未验证事件总线、配置加载和附件初始化是否按预期发生。
- `修复动作`: 首轮检查入口构造函数和注册调用链，必要时补齐缺失的注册调用或调整初始化顺序。
- `验证方式`: 运行启动检查，并执行 `ModInfoTest` 与 `ConfigsTest`，确认配置、物品和附件注册都被触发。
- `结论`: 先作为迁移检查清单保留，待启动验证完成后再定性。

## framework
### Entry 1: Box2D GUI 边界回归检查
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/framework/Point2D.java`, `src/main/java/org/hhoa/mc/item_information/framework/Box2D.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/framework/Box2D.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/client/gui/MobDictionaryGui.java`
- `预期行为`: `Box2D` 的几何判断应继续支撑 GUI 命中区域与布局计算，避免 1.21 客户端界面出现点击范围偏移。
- `当前状态`: `Verified`
- `发现的问题`: Box2D 是 `mobdictionary/client/gui` 命中检测的直接依赖。按计划播种记录：invalid bound validation 曾经写反，导致非法边界没有被正确拦截；该崩溃修复已经在 `e105b64` 落地。
- `修复动作`: 保留 `e105b64` 中对 invalid bound validation 反转问题的修复，并继续将 `Box2D` 作为 GUI 命中区域的基础回归面。
- `验证方式`: 运行 `./gradlew test --tests org.hhoa.mc.item_information.framework.Box2DTest`，再结合 `MobDictionaryGui` 的手动界面命中检查确认结果。
- `结论`: 该崩溃修复已按计划验证并接受，invalid bound validation 反转导致的崩溃问题已在 `e105b64` 修复完成，`Box2D` 条目当前为 `Verified`。

### Entry 2: utils 运行时兼容性检查
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/utils/EntityUtils.java`, `src/main/java/org/hhoa/mc/item_information/utils/GameUtils.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/utils/EntityUtils.java`, `M src/main/java/org/hhoa/mc/item_information/utils/GameUtils.java`
- `预期行为`: `EntityUtils` 的 attribute-holder 迁移应继续返回正确属性值或默认值，`GameUtils` 的 server-dist detection 应继续为运行时分支提供正确结果，不让依赖它们的功能静默偏离。
- `当前状态`: `Pending`
- `发现的问题`: `EntityUtils` 与 `GameUtils` 都在 1.21 diff 中被修改，但当前账本还没有单独记录它们对属性读取和 server-dist detection 的运行时影响，存在被 feature 级验证遗漏的风险。
- `修复动作`: 将 `EntityUtils` 绑定到实体属性读取相关验证，将 `GameUtils` 绑定到服务端分支与客户端分支验证；必要时补做 dedicated-server 启动检查或消费方回归。
- `验证方式`: 结合 `mobdictionary` 的实体数据和物品交互验证检查 attribute-holder migration，再通过服务端启动或 server-only 路径回归确认 `GameUtils` 的 server-dist detection 正常。
- `结论`: utils/runtime 条目已补入账本，后续必须随消费方验证一并回填结果，避免 `EntityUtils` 与 `GameUtils` 被静默跳过。

## itemtooltip
### Entry 1: ItemTooltip 服务拆分首轮验证
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltip.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipForgeEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipModEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/item/ItemInfo.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/kaymap/ItemTooltipKeyMappingRegistry.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/parser/ItemInfoParser.java`, `src/main/java/org/hhoa/mc/item_information/itemtooltip/item/TAG.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltip.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipForgeEventsHandler.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipModEventsHandler.java`, `A src/main/java/org/hhoa/mc/item_information/itemtooltip/ItemTooltipService.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/item/ItemInfo.java`, `M src/main/java/org/hhoa/mc/item_information/itemtooltip/kaymap/ItemTooltipKeyMappingRegistry.java`
- `预期行为`: 物品提示解析、格式化和按键开关行为在拆分出 `ItemTooltipService` 后仍保持与 1.20.x 一致。
- `当前状态`: `Pending`
- `发现的问题`: 服务拆分是当前 diff 的核心变化，但尚未确认事件处理器、按键映射和解析器之间的职责边界是否与旧行为一致。
- `修复动作`: 对照旧实现检查服务调用链，必要时补齐事件到服务的委派或修正 key mapping 注册时机。
- `验证方式`: 运行 `./gradlew test --tests org.hhoa.mc.item_information.itemtooltip.ItemTooltipTest --tests org.hhoa.mc.item_information.itemtooltip.ItemTooltipServiceTest`，并在客户端手动检查提示开关。
- `结论`: 先作为待验证条目保留，确认服务拆分没有行为漂移后再标记。

## mobdictionary
### Entry 1: 数据与网络迁移总览
- `1.20 来源`: `src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionary.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/EntityManager.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryFMLEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryForgeEventsHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/command/MobDictionaryCommand.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/recipes/MobDictionaryRecipeProvider.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobDatas.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobSavedData.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDataItem.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDictionaryItem.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityImpl.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityProvider.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/IFirstLoginCapability.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapability.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityImpl.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityProvider.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/PacketHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessage.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessageHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessage.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessageHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessage.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessageHandler.java`, `src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/SyncDataMessage.java`
- `1.21 对应实现`: `M src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionary.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/EntityManager.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryFMLEventsHandler.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/MobDictionaryForgeEventsHandler.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/command/MobDictionaryCommand.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/recipes/MobDictionaryRecipeProvider.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobDatas.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/data/MobSavedData.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDataItem.java`, `M src/main/java/org/hhoa/mc/item_information/mobdictionary/item/MobDictionaryItem.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/attachment/ModAttachments.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryPayloadHandlers.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/MobDictionaryPayloads.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/RegisterMobPayload.java`, `A src/main/java/org/hhoa/mc/item_information/mobdictionary/network/SyncMobDataPayload.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityImpl.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/FirstLoginCapabilityProvider.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/IFirstLoginCapability.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapability.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityImpl.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/capabilities/MobDataCapabilityProvider.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/PacketHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessage.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/register/RegisterMobMessageHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessage.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ClientSyncDataMessageHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessage.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/ServerSyncDataMessageHandler.java`, `D src/main/java/org/hhoa/mc/item_information/mobdictionary/network/packet/syncdata/SyncDataMessage.java`
- `预期行为`: 数据持久化、同步、命令入口、配方生成，以及解锁和实体注册流程都应完成从 capability + packet 到 attachment + payload 的迁移，同时保持 1.20.x 的功能行为。
- `当前状态`: `Pending`
- `发现的问题`: 当前 diff 显示核心存储和网络层已经重构，但还没有验证附件数据流、payload 编解码、`command` 执行路径、`recipes` 生成，以及解锁和 entity-registration flow 是否全部迁移完成。
- `修复动作`: 逐步核对保存数据、同步载荷、命令入口、配方 provider 和 unlock/entity-registration flow；必要时补齐 payload 注册、附件读写转换，或修正命令与配方生成挂接。
- `验证方式`: 运行 `./gradlew test --tests org.hhoa.mc.item_information.mobdictionary.data.MobSavedDataTest --tests org.hhoa.mc.item_information.mobdictionary.item.MobDataItemTest --tests org.hhoa.mc.item_information.mobdictionary.network.PayloadCodecTest`，并补做 `command`、`recipes` 与 unlock/entity-registration flow 的手动验证。
- `结论`: 该迁移范围大，继续保持为首轮待验条目。

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
- `1.21 对应实现`: `M src/main/resources/assets/entity_information/lang/en_us.json`, `M src/main/resources/assets/entity_information/lang/zh_cn.json`, `src/main/resources/assets/entity_information/item_infos/minecraft/*.json`, `src/main/resources/assets/entity_information/models/item/*.json`, `src/main/resources/assets/entity_information/textures/gui/*`, `src/main/resources/assets/entity_information/textures/item/*`
- `预期行为`: 语言键、模型、纹理和 `item_infos` 数据在 1.21 运行时仍应按旧路径解析，不引入缺失资源或错误键名。
- `当前状态`: `Pending`
- `发现的问题`: 语言文件已变化，但尚未确认新增或迁移后的键是否与代码中的翻译键和资源引用完全匹配。
- `修复动作`: 在首轮验证中对照代码引用检查资源命名，必要时修正语言键、模型引用或缺失纹理。
- `验证方式`: 运行客户端并检查资源加载日志，同时手动打开相关物品和界面确认资源解析正常。
- `结论`: 资源层先保持待验证状态，待运行时检查后更新。

## generated resources
### Entry 1: 数据生成目录重排验证
- `1.20 来源`: `src/generated/resources/data/entity_information/advancements/recipes/dictionary.json`, `src/generated/resources/data/entity_information/recipes/dictionary.json`
- `1.21 对应实现`: `A src/generated/resources/data/entity_information/advancement/recipes/tools/dictionary.json`, `D src/generated/resources/data/entity_information/advancements/recipes/dictionary.json`, `A src/generated/resources/data/entity_information/recipe/dictionary.json`, `D src/generated/resources/data/entity_information/recipes/dictionary.json`
- `预期行为`: 数据生成输出应与 NeoForge 1.21 当前 provider 期望的目录结构一致，生成后的配方与进度文件能被正常发现。
- `当前状态`: `Pending`
- `发现的问题`: 目录从复数路径迁移到单数路径，但还没有确认 provider、数据生成任务和运行时查找逻辑已经全部同步。
- `修复动作`: 对照 `MobDictionaryRecipeProvider` 和数据生成任务检查输出目录，若存在路径不一致则修正 provider 或生成目标。
- `验证方式`: 运行 `./gradlew runData` 并核对输出目录与加载结果。
- `结论`: 首轮以目录迁移检查为主，未验证前不接受该 diff。

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
