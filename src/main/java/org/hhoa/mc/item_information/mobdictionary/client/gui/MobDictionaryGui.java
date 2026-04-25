package org.hhoa.mc.item_information.mobdictionary.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.ModInfo;
import org.hhoa.mc.item_information.framework.Box2D;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.ChatText;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.mobdictionary.network.Event;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;
import org.hhoa.mc.item_information.mobdictionary.network.MobDictionaryGuiButtonClickEvent;
import org.hhoa.mc.item_information.mobdictionary.network.PacketHandler;
import org.hhoa.mc.item_information.utils.EntityUtils;
import org.hhoa.mc.item_information.utils.PlayerUtils;
import org.hhoa.mc.item_information.utils.TextRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class MobDictionaryGui extends Screen {
    private static final ResourceLocation dictionaryResource =
            EntityInformation.location("textures/gui/dictionary.png");

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

    protected final float entityInitScale = 22F;
    protected final float entityMinScale = 20F;
    protected final float entityMaxScale = 26F;
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
        Button convertedPaperButton = getButton(8);

        lightningBolt = EntityType.LIGHTNING_BOLT.create(Minecraft.getInstance().level);
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

    private @NotNull Button getButton(int size) {
        MutableComponent empty = Component.empty();
        for (int i = 0; i < tooltipStringList.size(); i++) {
            if (i != tooltipStringList.size() - 1) {
                empty.append("\n");
            }
            empty.append(tooltipStringList.get(i).getTextComponent());
        }
        Tooltip tooltip = Tooltip.create(empty);
        Button convertedPaperButton =
                new ImageButton(
                        originX + 19,
                        originY + 136,
                        size,
                        size,
                        0,
                        0,
                        size,
                        new ResourceLocation(ModInfo.ID, "textures/gui/button.png"),
                        size,
                        2 * size,
                        this::convertedPaperButtonOnPress,
                        Component.literal("B"));
        convertedPaperButton.setTooltip(tooltip);
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
                        MobDictionaryGuiButtonClickEvent mobDictionaryGuiButtonClickEvent =
                                new MobDictionaryGuiButtonClickEvent(entityType.getDescriptionId());
                        PacketHandler.CHANNEL.sendToServer(mobDictionaryGuiButtonClickEvent);
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
        this.drawGuiBackgroundLayer(matrixStack);

        if (this.entityTypes.length > 0) {
            initDisplayEntity(this.entityTypes[this.currentNo].getB());
        }

        if (displayEntity != null) {
            drawMobModel();
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
        matrixStack.blit(dictionaryResource, originX, originY, 0, 0, this.xSize, this.ySize);
    }

    private void drawLockMobInfo(GuiGraphics matrixStack) {
        this.font.drawInBatch(
                "??????",
                originX + 19,
                originY + 85,
                this.stringColor,
                false,
                new Matrix4f(),
                matrixStack.bufferSource(),
                Font.DisplayMode.NORMAL,
                0,
                15728880);
    }

    public void drawUnLockMobInfo(GuiGraphics matrixStack) {
        List<Tuple<String, String>> kvList = new ArrayList<>();
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.MAX_HEALTH.getDescriptionId()),
                        String.format(":%.1f", displayEntity.getMaxHealth())));
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.ARMOR.getDescriptionId()),
                        String.format(":%d", displayEntity.getArmorValue())));
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.ATTACK_DAMAGE.getDescriptionId()),
                        String.format(
                                ":%.1f",
                                EntityUtils.getEntityAttribute(
                                        displayEntity, Attributes.ATTACK_DAMAGE))));
        kvList.add(
                new Tuple<>(
                        I18n.get(Attributes.MOVEMENT_SPEED.getDescriptionId()),
                        String.format(
                                ":%.1f",
                                EntityUtils.getEntityAttribute(
                                        displayEntity, Attributes.MOVEMENT_SPEED))));

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
                MobDatas.getRegisteredMobCountOnClient()
                        + "/"
                        + MobDictionary.getEntityManager().getAllMobCount();
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
            displayName = Language.getInstance().getOrDefault(entityType.getDescriptionId());
        } else {
            displayName = Texts.UNKNOWN_BIOLOGY.getText() + id;
        }
        return displayName;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mobBox.isInBox(mouseX, mouseY)) {
            entityScale = (float) delta + entityScale;
            entityScale = Math.max(entityScale, entityMinScale);
            entityScale = Math.min(entityScale, entityMaxScale);
        }

        nameListScroll(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseX, delta);
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

    protected void drawMobModel() {
        EntityRenderDispatcher entityRenderDispatcher =
                Minecraft.getInstance().getEntityRenderDispatcher();

        PoseStack poseStack = new PoseStack();
        float scale = entityScale;
        poseStack.translate(originX + 49, originY + 70, 10);
        poseStack.scale(scale, scale, scale);
        Quaternionf rotationZ = new Quaternionf().rotateAxis((float) Math.toRadians(180), 0, 0, 1);
        poseStack.mulPose(rotationZ);
        Quaternionf rotationYq =
                new Quaternionf().rotateAxis((float) Math.toRadians(-rotationX), 0, 1, 0);
        poseStack.mulPose(rotationYq);
        Quaternionf rotationX =
                new Quaternionf().rotateAxis((float) Math.toRadians(rotationY), 1, 0, 0);
        poseStack.mulPose(rotationX);

        int light;

        if (!isUnLock(displayEntity)) {
            light = LightTexture.pack(0, 0);
        } else {
            light = LightTexture.pack(15, 15);
        }

        MultiBufferSource.BufferSource bufferSource =
                Minecraft.getInstance().renderBuffers().bufferSource();

        EntityRenderer<? super Entity> renderer =
                entityRenderDispatcher.getRenderer(this.displayEntity);
        renderer.render(this.displayEntity, 0.0F, 1.0F, poseStack, bufferSource, light);

        bufferSource.endBatch();
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
        } else {
            if (displayEntity == null || displayEntity.getType() != entityResourceLocation) {
                if (displayEntity != null) {
                    displayEntity.discard();
                }
                displayEntity = (LivingEntity) entityResourceLocation.create(this.minecraft.level);
            }
            setEntityStatus();
        }
    }

    private void setEntityStatus() {
        if (displayEntity.getType() == EntityType.CREEPER) {
            MobStatusEnum mobStatus = MobStatusEnum.values()[currentMobStatus];
            if (Objects.requireNonNull(mobStatus) == MobStatusEnum.THUNDER) {
                displayEntity.thunderHit(
                        ServerLifecycleHooks.getCurrentServer().overworld(), lightningBolt);
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
