package org.hhoa.mc.item_information.mobdictionary.client.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.renderstate.RenderStateExtensions;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.framework.Box2D;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.ChatText;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.mobdictionary.network.Event;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;
import org.hhoa.mc.item_information.mobdictionary.network.MobDictionaryButtonPayload;
import org.hhoa.mc.item_information.utils.PlayerUtils;
import org.hhoa.mc.item_information.utils.TextRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MobDictionaryGui extends Screen {
    private static final ResourceLocation dictionaryResource =
            EntityInformation.location("textures/gui/dictionary.png");
    private static final MobDictionaryEntityPreviewState ENTITY_PREVIEW_STATE =
            new MobDictionaryEntityPreviewState(20.0F, 26.0F);
    private static final int BACKDROP_TINT = 0x66000000;

    protected int xSize = 176;
    protected int ySize = 166;
    private int originX;
    private int originY;

    protected int stringColor = 0x303030;
    protected int stringHeight;
    protected int stringYMargin = 6;
    protected int scrollAmount = 0;
    protected int sizeNameAreaX = 60;
    protected int currentNo = 0;
    protected int topEdge = 20;
    protected int bottomEdge = 11;
    protected int namesCenterOffsetX = 126;

    private Tuple<Integer, EntityType<?>>[] entityTypes;

    protected static LivingEntity displayEntity;
    protected static LightningBolt lightningBolt;

    protected final float entityInitScale = ENTITY_PREVIEW_STATE.clampScale(22F);
    protected float entityScale = entityInitScale;
    protected double yaw = 0.0D;
    protected double yaw2 = 0.0D;
    private final float initialRotationX = 0;
    private final float initialRotationY = 0;
    private float rotationX = initialRotationX;
    private float rotationY = initialRotationY;
    private boolean isMobDragging;

    private static final List<ChatText> tooltipStringList =
            Arrays.asList(Texts.OUTPUT_PIECE, Texts.NEED_A_PAPER);
    private HashSet<EntityType<?>> unLockedMobTypes;
    private Box2D mobBox;
    private int currentMobStatus = 0;
    private int currentTicks = 0;
    private final int statusDurationSeconds = 2;
    private final List<String> eventHandlerIds = new ArrayList<>();

    public MobDictionaryGui() {
        super(Texts.DICTIONARY_NAME.getTextComponent());
    }

    @Override
    public void init() {
        this.stringHeight = font.lineHeight;
        this.originX = (this.width - this.xSize) / 2;
        this.originY = (this.height - this.ySize) / 2;
        this.mobBox = new Box2D(originX + 19, originY + 12, originX + 78, originY + 82);

        displayEntity = null;
        initUnLockedMobTypes();

        eventHandlerIds.add(
                MobDictionary.getDispatcher()
                        .registerQuestHandler(
                                Arrays.asList(EventType.DELETE, EventType.PUT),
                                this::processMobDictionaryGuiButtonClickEventCallBack));
        Button convertedPaperButton = getButton();

        if (Minecraft.getInstance().level != null) {
            lightningBolt =
                    EntityType.LIGHTNING_BOLT.create(
                            Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
        }
        this.addRenderableWidget(convertedPaperButton);
    }

    private void processMobDictionaryGuiButtonClickEventCallBack(Event event) {
        initUnLockedMobTypes();
    }

    private void initUnLockedMobTypes() {
        Set<EntityType<? extends LivingEntity>> allEntities =
                MobDictionary.getEntityManager().getAllEntities();
        this.unLockedMobTypes = new HashSet<>();
        Set<String> unLockMobNamesOnClient = MobDatas.getUnLockMobNamesOnClient();
        List<Tuple<Integer, EntityType<?>>> unLockedMobTypesWithId = new ArrayList<>();
        List<Tuple<Integer, EntityType<?>>> lockedMobTypesWithId = new ArrayList<>();
        int i = 1;
        for (EntityType<? extends LivingEntity> entityType : allEntities) {
            if (unLockMobNamesOnClient.contains(entityType.getDescriptionId())) {
                unLockedMobTypes.add(entityType);
                unLockedMobTypesWithId.add(new Tuple<>(i, entityType));
            } else {
                lockedMobTypesWithId.add(new Tuple<>(i, entityType));
            }
            i++;
        }
        ArrayList<Tuple<Integer, EntityType<?>>> entityTypes = new ArrayList<>();
        entityTypes.addAll(
                unLockedMobTypesWithId.stream()
                        .sorted(
                                (o1, o2) ->
                                        CharSequence.compare(
                                                o1.getB().getDescriptionId(),
                                                o2.getB().getDescriptionId()))
                        .toList());
        entityTypes.addAll(
                lockedMobTypesWithId.stream()
                        .sorted(
                                (o1, o2) ->
                                        CharSequence.compare(
                                                o1.getB().getDescriptionId(),
                                                o2.getB().getDescriptionId()))
                        .toList());
        this.entityTypes = entityTypes.toArray(new Tuple[0]);
    }

    private @NotNull Button getButton() {
        MutableComponent empty = Component.empty();
        for (int i = 0; i < tooltipStringList.size(); i++) {
            if (i != tooltipStringList.size() - 1) {
                empty.append("\n");
            }
            empty.append(tooltipStringList.get(i).getTextComponent());
        }
        Tooltip tooltip = Tooltip.create(empty);
        Button convertedPaperButton =
                new MobDictionaryConvertButton(
                        originX + 19, originY + 134, this::convertedPaperButtonOnPress, tooltip);
        convertedPaperButton.active = this.entityTypes.length > 0;
        return convertedPaperButton;
    }

    @Override
    public boolean mouseDragged(
            double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMobDragging) {
            rotationX += deltaX * 0.5f;
            rotationY += deltaY * 0.5f;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void convertedPaperButtonOnPress(Button button) {
        if (this.entityTypes.length > 0) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack paper = new ItemStack(Items.PAPER, 1);
                ItemStack feather = new ItemStack(Items.FEATHER, 1);
                EntityType<?> entityType = this.entityTypes[this.currentNo].getB();
                if (MobDatas.containsMobNameOnClient(entityType.getDescriptionId())) {
                    if (PlayerUtils.hasItemCount(player, paper)
                            && PlayerUtils.hasItemCount(player, feather)) {
                        MobDictionaryButtonPayload mobDictionaryButtonPayload =
                                new MobDictionaryButtonPayload(entityType.getDescriptionId());
                        ClientPacketDistributor.sendToServer(mobDictionaryButtonPayload);
                    } else {
                        player.displayClientMessage(
                                Texts.NOT_HAVE_ITEM
                                        .withTranslatableTexts(
                                                I18n.get(Items.PAPER.getDescriptionId())
                                                        + "+"
                                                        + I18n.get(
                                                                Items.FEATHER.getDescriptionId()))
                                        .getTextComponent(),
                                false);
                    }
                } else {
                    player.displayClientMessage(
                            Texts.UNREGISTER_NOT_EXIST.withTranslatableTexts().getTextComponent(),
                            false);
                }
            }
        }
    }

    @Override
    public void onClose() {
        for (String eventHandlerId : eventHandlerIds) {
            MobDictionary.getDispatcher().removeEventHandler(eventHandlerId);
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        currentTicks += 1;
    }

    @Override
    public void render(
            @NotNull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.yaw2 = this.yaw;
        this.renderTransparentBackground(matrixStack);
        this.drawGuiBackgroundLayer(matrixStack);

        if (this.entityTypes.length > 0) {
            initDisplayEntity(this.entityTypes[this.currentNo].getB());
        }

        if (displayEntity != null) {
            drawMobModel(matrixStack);
            if (isUnLock(displayEntity)) {
                drawUnLockMobInfo(matrixStack);
            } else {
                // TODO 出现地点
                drawLockMobInfo(matrixStack);
            }
        }

        this.drawMobNames(matrixStack, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.yaw = 2.0D + yaw2 + (yaw2 - yaw) * partialTicks;
    }

    @Override
    public void renderBackground(
            @NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    @Override
    protected void renderBlurredBackground(GuiGraphics guiGraphics) {
        // NeoForge 1.21 screens default to a menu blur backdrop. The dictionary keeps its own
        // translucent overlay so the book and text stay crisp when compatibility layers call
        // Screen background hooks.
    }

    @Override
    public void renderTransparentBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(0, 0, this.width, this.height, BACKDROP_TINT);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isMobDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && mobBox.isInBox(mouseX, mouseY)) {
            isMobDragging = true;
        }
        int count;

        for (int i = 0; i < this.entityTypes.length; i++) {
            if ((this.topEdge + ((i + 1) * (this.stringHeight + this.stringYMargin)))
                    > (this.ySize - this.bottomEdge)) {
                break;
            }

            count = i + this.scrollAmount;

            if (this.isMouseInArea(
                    mouseX,
                    mouseY,
                    originX + this.namesCenterOffsetX - this.sizeNameAreaX / 2,
                    originX + this.namesCenterOffsetX + this.sizeNameAreaX / 2,
                    originY + this.topEdge + (i * (this.stringHeight + this.stringYMargin)),
                    originY
                            + this.topEdge
                            + (i * (this.stringHeight + this.stringYMargin) + this.stringHeight))) {
                if (this.currentNo != count) {
                    initRenderParams();
                }

                this.currentNo = count;
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void initRenderParams() {
        this.entityScale = entityInitScale;
        this.rotationX = initialRotationX;
        this.rotationY = initialRotationY;
    }

    public void drawGuiBackgroundLayer(GuiGraphics matrixStack) {
        matrixStack.blit(
                RenderPipelines.GUI_TEXTURED,
                dictionaryResource,
                originX,
                originY,
                0.0F,
                0.0F,
                this.xSize,
                this.ySize,
                MobDictionaryEntityPreviewState.DICTIONARY_TEXTURE_SIZE,
                MobDictionaryEntityPreviewState.DICTIONARY_TEXTURE_SIZE);
    }

    private void drawLockMobInfo(GuiGraphics matrixStack) {
        TextRenderer.drawSimpleText(
                this.font,
                matrixStack,
                MobDictionaryTextContent.lockedDisplayName(
                        Texts.UNKNOWN_BIOLOGY.getText(),
                        this.entityTypes[this.currentNo].getA()),
                originX + 19,
                originY + 85,
                this.stringColor);
    }

    public void drawUnLockMobInfo(GuiGraphics matrixStack) {
        List<Tuple<String, String>> kvList = new ArrayList<>();
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.MAX_HEALTH.value().getDescriptionId()),
                        String.format(":%.1f", displayEntity.getMaxHealth())));
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.ARMOR.value().getDescriptionId()),
                        String.format(":%d", displayEntity.getArmorValue())));
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.ATTACK_DAMAGE.value().getDescriptionId()),
                        String.format(
                                ":%.1f",
                                MobDictionaryEntityPreviewState.attributeValueOrZero(
                                        displayEntity.getAttributes(), Attributes.ATTACK_DAMAGE))));
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.MOVEMENT_SPEED.value().getDescriptionId()),
                        String.format(
                                ":%.1f",
                                MobDictionaryEntityPreviewState.attributeValueOrZero(
                                        displayEntity.getAttributes(), Attributes.MOVEMENT_SPEED))));

        int xStart = originX + 19, yStart = originY + 85, dY = 12, currentY = yStart;
        for (Tuple<String, String> tuple : kvList) {
            TextRenderer.drawSimpleText(
                    font, matrixStack, tuple.getA(), originX + 19, currentY, this.stringColor);
            TextRenderer.drawSimpleText(
                    font,
                    matrixStack,
                    tuple.getB(),
                    xStart + font.width(tuple.getA()) + 2,
                    currentY,
                    this.stringColor);
            currentY = dY + currentY;
        }
    }

    public void drawMobNames(GuiGraphics matrixStack, int mouseX, int mouseY) {
        String str =
                MobDictionaryTextContent.progressText(
                        MobDatas.getRegisteredMobCountOnClient(),
                        MobDictionary.getEntityManager().getAllMobCount());
        TextRenderer.drawSimpleText(
                this.font,
                matrixStack,
                str,
                originX + namesCenterOffsetX - (float) this.font.width(str) / 2,
                originY + 8,
                this.stringColor);

        if (this.entityTypes.length > 0) {
            for (int i = 0; i < this.entityTypes.length; i++) {
                if ((this.topEdge + ((i + 1) * (this.stringHeight + this.stringYMargin)))
                        > (this.ySize - this.bottomEdge)) {
                    break;
                }

                int var1 = i + this.scrollAmount;
                EntityType<?> entityType = this.entityTypes[var1].getB();
                Integer id = this.entityTypes[var1].getA();
                boolean unLock = isUnLock(entityType);
                String displayName = getDisplayName(entityType, unLock, id);

                int stringWidth = this.font.width(displayName);
                int color =
                        var1 == this.currentNo
                                        || isMouseInArea(
                                                mouseX,
                                                mouseY,
                                                originX
                                                        + this.namesCenterOffsetX
                                                        - this.sizeNameAreaX / 2,
                                                originX
                                                        + this.namesCenterOffsetX
                                                        + this.sizeNameAreaX / 2,
                                                originY
                                                        + this.topEdge
                                                        + (i
                                                                * (this.stringHeight
                                                                        + this.stringYMargin)),
                                                originY
                                                        + this.topEdge
                                                        + (i
                                                                        * (this.stringHeight
                                                                                + this
                                                                                        .stringYMargin)
                                                                + this.stringHeight))
                                ? 0xffffff
                                : unLock ? this.stringColor : 0x404040;
                TextRenderer.drawSimpleText(
                        this.font,
                        matrixStack,
                        displayName,
                        originX + namesCenterOffsetX - (float) stringWidth / 2,
                        originY + this.topEdge + (i * (this.stringHeight + this.stringYMargin)),
                        color);
            }
        }
    }

    private String getDisplayName(EntityType<?> entityType, boolean unLock, Integer id) {
        String displayName;
        if (unLock) {
            displayName = net.minecraft.locale.Language.getInstance().getOrDefault(entityType.getDescriptionId());
        } else {
            displayName = MobDictionaryTextContent.lockedDisplayName(Texts.UNKNOWN_BIOLOGY.getText(), id);
        }
        return displayName;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mobBox.isInBox(mouseX, mouseY)) {
            entityScale = ENTITY_PREVIEW_STATE.clampScale((float) scrollY + entityScale);
        }

        nameListScroll(mouseX, mouseY, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void nameListScroll(double mouseX, double mouseY, double delta) {
        if (this.entityTypes.length > 0) {
            if (this.entityTypes.length
                            > (this.ySize - this.bottomEdge)
                                    / (this.stringHeight + this.stringYMargin)
                    && this.isMouseInArea(
                            mouseX,
                            mouseY,
                            originX + this.namesCenterOffsetX - this.sizeNameAreaX / 2,
                            originX + this.namesCenterOffsetX + this.sizeNameAreaX / 2,
                            originY + this.topEdge,
                            originY + this.ySize - this.bottomEdge)) {

                if (delta < 0) {
                    this.scrollAmount += 1;
                    if (this.scrollAmount
                            > this.entityTypes.length
                                    - (this.ySize - this.bottomEdge)
                                            / (this.stringHeight + this.stringYMargin)) {
                        this.scrollAmount =
                                this.entityTypes.length
                                        - (this.ySize - this.bottomEdge)
                                                / (this.stringHeight + this.stringYMargin);
                    }
                } else if (delta > 0) {
                    this.scrollAmount -= 1;

                    if (this.scrollAmount < 0) {
                        this.scrollAmount = 0;
                    }
                }
            }
        }
    }

    protected void drawMobModel(GuiGraphics matrixStack) {
        MobDictionaryEntityPreviewState.PreviewEntityRotation previewRotation =
                ENTITY_PREVIEW_STATE.previewEntityRotation(rotationX, rotationY);
        float previousYRot = displayEntity.getYRot();
        float previousXRot = displayEntity.getXRot();
        float previousBodyRot = displayEntity.yBodyRot;
        float previousHeadRot = displayEntity.yHeadRot;
        float previousHeadRotO = displayEntity.yHeadRotO;
        Quaternionf rotationZ = new Quaternionf().rotateAxis((float) Math.toRadians(180), 0, 0, 1);
        Quaternionf cameraRotation =
                new Quaternionf().rotateAxis((float) Math.toRadians(-rotationY), 1, 0, 0);
        Vector3f translation = new Vector3f(0.0F, 0.0F, 0.0F);
        boolean unlocked = isUnLock(displayEntity);
        MobDictionaryEntityPreviewState.PreviewBounds previewBounds =
                MobDictionaryEntityPreviewState.previewBounds(
                                (int) mobBox.getMinX(),
                                (int) mobBox.getMinY(),
                                (int) mobBox.getMaxX() + 1,
                                (int) mobBox.getMaxY() + 1,
                                originX + 49,
                                originY + 70)
                        .expanded(7, 10, 7, 4);
        float renderScale =
                ENTITY_PREVIEW_STATE.fitScale(
                        entityScale,
                        displayEntity.getBbWidth(),
                        displayEntity.getBbHeight(),
                        previewBounds);

        try {
            displayEntity.setYRot(previewRotation.yRot());
            displayEntity.setXRot(previewRotation.xRot());
            displayEntity.yBodyRot = previewRotation.bodyRot();
            displayEntity.yHeadRot = previewRotation.headRot();
            displayEntity.yHeadRotO = previewRotation.headRotO();

            Vector3f entityTranslation =
                    new Vector3f(
                            translation.x,
                            translation.y + displayEntity.getBbHeight() / 2.0F,
                            translation.z);
            if (unlocked) {
                InventoryScreen.renderEntityInInventory(
                        matrixStack,
                        previewBounds.x1(),
                        previewBounds.y1(),
                        previewBounds.x2(),
                        previewBounds.y2(),
                        renderScale,
                        entityTranslation,
                        rotationZ,
                        cameraRotation,
                        displayEntity);
            } else {
                matrixStack.submitPictureInPictureRenderState(
                        new LockedEntityRenderState(
                                createEntityRenderState(displayEntity),
                                entityTranslation,
                                rotationZ,
                                cameraRotation,
                                previewBounds.x1(),
                                previewBounds.y1(),
                                previewBounds.x2(),
                                previewBounds.y2(),
                                renderScale,
                                matrixStack.peekScissorStack()));
            }
        } finally {
            displayEntity.setYRot(previousYRot);
            displayEntity.setXRot(previousXRot);
            displayEntity.yBodyRot = previousBodyRot;
            displayEntity.yHeadRot = previousHeadRot;
            displayEntity.yHeadRotO = previousHeadRotO;
        }

    }

    private static EntityRenderState createEntityRenderState(LivingEntity entity) {
        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer entityRenderer = renderDispatcher.getRenderer(entity);
        EntityRenderState renderState = entityRenderer.createRenderState();
        entityRenderer.extractRenderState(entity, renderState, 1.0F);
        RenderStateExtensions.onUpdateEntityRenderState(entityRenderer, entity, renderState);
        renderState.hitboxesRenderState = null;
        return renderState;
    }

    private boolean isUnLock(LivingEntity displayEntity) {
        return unLockedMobTypes.contains(displayEntity.getType());
    }

    private boolean isUnLock(EntityType<?> entityType) {
        return unLockedMobTypes.contains(entityType);
    }

    private void initDisplayEntity(EntityType<?> entityResourceLocation) {
        if (entityResourceLocation == null) {
            displayEntity = null;
            return;
        }

        if (displayEntity == null || displayEntity.getType() != entityResourceLocation) {
            if (displayEntity != null) {
                displayEntity.discard();
            }
            displayEntity =
                    this.minecraft.level == null
                            ? null
                            : (LivingEntity)
                                    entityResourceLocation.create(
                                            this.minecraft.level, EntitySpawnReason.COMMAND);
        }

        if (displayEntity == null) {
            return;
        }

        setEntityStatus();
    }

    private void setEntityStatus() {
        if (displayEntity == null) {
            return;
        }

        if (displayEntity.getType() == EntityType.CREEPER) {
            MobStatusEnum mobStatus = MobStatusEnum.values()[currentMobStatus];
            if (mobStatus == MobStatusEnum.THUNDER
                    && Minecraft.getInstance().getSingleplayerServer() != null
                    && lightningBolt != null) {
                displayEntity.thunderHit(
                        Minecraft.getInstance().getSingleplayerServer().overworld(),
                        lightningBolt);
                displayEntity.heal(20);
            }
        }
        if (currentTicks % 20 * statusDurationSeconds == 0) {
            currentMobStatus = (currentMobStatus + 1) % MobStatusEnum.values().length;
        }
    }

    protected boolean isMouseInArea(double mouseX, double mouseY, int x1, int x2, int y1, int y2) {
        return x1 <= mouseX && mouseX < x2 && y1 <= mouseY && mouseY < y2;
    }

    enum MobStatusEnum {
        DEFAULT,
        THUNDER
    }
}
