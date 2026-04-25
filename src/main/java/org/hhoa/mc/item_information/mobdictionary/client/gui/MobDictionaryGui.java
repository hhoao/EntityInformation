package org.hhoa.mc.item_information.mobdictionary.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.antlr.v4.runtime.misc.NotNull;
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
import org.hhoa.mc.item_information.utils.Worlds;

public class MobDictionaryGui extends Screen {
    private static final net.minecraft.util.ResourceLocation dictionaryResource =
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
    protected static LightningBoltEntity lightningBolt;

    protected final float entityInitScale = 22F;
    protected final float entityMinScale = 20F;
    protected final float entityMaxScale = 26F;
    protected float entityScale = entityInitScale;
    protected double yaw = 0.0D;
    protected double yaw2 = 0.0D;
    private float rotationX = 0;
    private float rotationY = 0;
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
        super(
                new StringTextComponent(
                        I18n.format(MobDictionary.mobDictionary.getTranslationKey())));
    }

    @Override
    protected void init() {
        // field_230712_o_: font
        this.stringHeight = this.font.FONT_HEIGHT;
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

        lightningBolt = EntityType.LIGHTNING_BOLT.create(Minecraft.getInstance().world);
        this.addButton(convertedPaperButton);
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
            if (unLockMobNamesOnClient.contains(entityType.getTranslationKey())) {
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
                        .sorted(Comparator.comparing(o -> o.getB().getTranslationKey()))
                        .collect(Collectors.toList()));
        entityTypes.addAll(
                lockedMobTypesWithId.stream()
                        .sorted(Comparator.comparing(o -> o.getB().getTranslationKey()))
                        .collect(Collectors.toList()));
        this.entityTypes = entityTypes.toArray(new Tuple[0]);
    }

    private @NotNull Button getButton(int size) {
        return new ImageButton(
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
                this::convertedPaperRenderToolTip,
                new StringTextComponent("B"));
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
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack paper = new ItemStack(Items.PAPER, 1);
                ItemStack feather = new ItemStack(Items.FEATHER, 1);
                EntityType<?> entityType = this.entityTypes[this.currentNo].getB();
                if (MobDatas.containsMobNameOnClient(entityType.getTranslationKey())) {
                    if (PlayerUtils.hasItemCount(player, paper)
                            && PlayerUtils.hasItemCount(player, feather)) {
                        MobDictionaryGuiButtonClickEvent mobDictionaryGuiButtonClickEvent =
                                new MobDictionaryGuiButtonClickEvent(
                                        entityType.getTranslationKey());
                        PacketHandler.CHANNEL.sendToServer(mobDictionaryGuiButtonClickEvent);
                    } else {
                        player.sendMessage(
                                Texts.NOT_HAVE_ITEM
                                        .withText(
                                                I18n.format(Items.PAPER.getTranslationKey())
                                                        + "+"
                                                        + I18n.format(
                                                                Items.FEATHER.getTranslationKey()))
                                        .getTextComponent(),
                                player.getUniqueID());
                    }
                } else {
                    player.sendMessage(
                            Texts.UNREGISTER_NOT_EXIST.withTranslatableTexts().getTextComponent(),
                            player.getUniqueID());
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

    private void convertedPaperRenderToolTip(
            Button button, MatrixStack postStack, int mouseX, int mouseY) {
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY - 12;

        int maxWidth = 0;
        for (ChatText chatMessage : tooltipStringList) {
            maxWidth = Math.max(font.getStringWidth(chatMessage.getText()), maxWidth);
        }

        fill(
                postStack,
                tooltipX - 3,
                tooltipY - 3,
                tooltipX + 3 + maxWidth + 10,
                tooltipY + 8 + font.FONT_HEIGHT * tooltipStringList.size(),
                0xF0100010);

        for (int i = 0; i < tooltipStringList.size(); i++) {
            ITextComponent text = tooltipStringList.get(i).getTextComponent();
            // draw
            font.func_243246_a(postStack, text, tooltipX, tooltipY + i * 10, 0xFFFFFF);
        }
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
            @NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
        this.rotationX = 0;
        this.rotationY = 0;
    }

    public void drawGuiBackgroundLayer(MatrixStack matrixStack) {
        Minecraft.getInstance().getTextureManager().bindTexture(dictionaryResource);
        // bill
        this.blit(matrixStack, originX, originY, 0, 0, this.xSize, this.ySize);
    }

    private void drawLockMobInfo(MatrixStack matrixStack) {
        this.font.drawString(matrixStack, "??????", originX + 19, originY + 85, this.stringColor);
    }

    public void drawUnLockMobInfo(MatrixStack matrixStack) {
        List<Tuple<String, String>> kvList = new ArrayList<>();
        kvList.add(
                new Tuple<>(
                        I18n.format(Attributes.MAX_HEALTH.getAttributeName()),
                        String.format(":%.1f", displayEntity.getMaxHealth())));
        kvList.add(
                new Tuple<>(
                        I18n.format(Attributes.ARMOR.getAttributeName()),
                        String.format(":%d", displayEntity.getTotalArmorValue())));
        kvList.add(
                new Tuple<>(
                        I18n.format(Attributes.ATTACK_DAMAGE.getAttributeName()),
                        String.format(
                                ":%.1f",
                                EntityUtils.getEntityAttribute(
                                        displayEntity, Attributes.ATTACK_DAMAGE))));
        kvList.add(
                new Tuple<>(
                        I18n.format(Attributes.MOVEMENT_SPEED.getAttributeName()),
                        String.format(
                                ":%.1f",
                                EntityUtils.getEntityAttribute(
                                        displayEntity, Attributes.MOVEMENT_SPEED))));

        int xStart = originX + 19, yStart = originY + 85, dY = 12, currentY = yStart;
        for (Tuple<String, String> tuple : kvList) {
            font.drawString(matrixStack, tuple.getA(), originX + 19, currentY, this.stringColor);
            font.drawString(
                    matrixStack,
                    tuple.getB(),
                    xStart + font.getStringWidth(tuple.getA()) + 2,
                    currentY,
                    this.stringColor);
            currentY = dY + currentY;
        }
    }

    public void drawMobNames(MatrixStack matrixStack, int mouseX, int mouseY) {
        String str =
                MobDatas.getRegisteredMobCountOnClient()
                        + "/"
                        + MobDictionary.getEntityManager().getAllMobCount();
        font.drawString(
                matrixStack,
                str,
                originX + namesCenterOffsetX - (float) font.getStringWidth(str) / 2,
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

                int stringWidth = font.getStringWidth(displayName);
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
                font.drawString(
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
            displayName =
                    LanguageMap.getInstance().getLanguageData().get(entityType.getTranslationKey());
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
        EntityRendererManager entityRenderDispatcher = Minecraft.getInstance().getRenderManager();

        MatrixStack poseStack = new MatrixStack();
        float scale = entityScale;
        poseStack.translate(originX + 49, originY + 70, 2);
        poseStack.scale(scale, scale, scale);
        poseStack.rotate(Vector3f.ZP.rotationDegrees(180F));
        poseStack.rotate(Vector3f.YP.rotationDegrees(-rotationX)); // X 轴旋转
        poseStack.rotate(Vector3f.XP.rotationDegrees(rotationY)); // Y 轴旋转

        int light;

        if (!isUnLock(displayEntity)) {
            light = LightTexture.packLight(0, 0);
        } else {
            light = LightTexture.packLight(15, 15);
        }

        IRenderTypeBuffer.Impl bufferSource =
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        EntityRenderer<? super Entity> renderer =
                entityRenderDispatcher.getRenderer(this.displayEntity);
        renderer.render(this.displayEntity, 0.0F, 1.0F, poseStack, bufferSource, light);

        bufferSource.finish();
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
                    displayEntity.remove();
                }
                displayEntity =
                        (LivingEntity) entityResourceLocation.create(this.getMinecraft().world);
            }
            setEntityStatus();
        }
    }

    private void setEntityStatus() {
        if (displayEntity.getType() == EntityType.CREEPER) {
            MobStatusEnum mobStatus = MobStatusEnum.values()[currentMobStatus];
            if (Objects.requireNonNull(mobStatus) == MobStatusEnum.THUNDER) {
                displayEntity.func_241841_a(
                        ServerLifecycleHooks.getCurrentServer().getWorld(Worlds.overworld),
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
