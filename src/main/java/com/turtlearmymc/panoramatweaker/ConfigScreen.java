package com.turtlearmymc.panoramatweaker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private final RotatingCubeMapRenderer backgroundRenderer;
    private final Screen parent;
    private final Config backupConfig;

    protected ConfigScreen(Screen parent) {
        super(Text.translatable("panorama_tweaker.title"));
        this.backgroundRenderer = new RotatingCubeMapRenderer(TitleScreen.PANORAMA_CUBE_MAP);
        this.parent = parent;
        this.backupConfig = new Config(PanoramaTweaker.config);
    }

    @Override
    protected void init() {
        final int y_padding = 28;

        ButtonWidget.builder(Text.translatable("gui.done"), (button) -> {
            this.saveConfig();
            client.setScreen(parent);
        }).position(this.width / 2 + 4, this.height - y_padding).size(150, 20).build();

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cancel"), (button) -> {
            this.restoreConfig();
            client.setScreen(parent);
        }).position(this.width / 2 - 154, this.height - y_padding).size(150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), (button) -> {
            this.saveConfig();
            client.setScreen(parent);
        }).position(this.width / 2 + 4, this.height - y_padding).size(150, 20).build());

        final int widgetCount = 6;
        final int widgetPadding = 76;

        int x = (this.width / 2) - 154;
        int y = ((this.height - widgetPadding) * 0) / widgetCount + y_padding;

        for (ClickableWidget child : (new OptionWidget(this.textRenderer,
                Text.translatable("panorama_tweaker.rotationSpeed"), x, y, 308, 20, Config.DEFAULT_ROTATION_SPEED,
                PanoramaTweaker.config.rotationSpeed, -10, 10, val -> PanoramaTweaker.config.rotationSpeed = val))
                        .children()) {
            this.addDrawableChild(child);
        }

        y = ((this.height - widgetPadding) * 1) / widgetCount + y_padding;
        for (ClickableWidget child : ((new OptionWidget(this.textRenderer,
                Text.translatable("panorama_tweaker.startingHorizontalAngle"), x, y, 308, 20,
                Config.DEFAULT_STARTING_HORIZONTAL_ANGLE, PanoramaTweaker.config.startingHorizontalAngle, -180, 180,
                val -> PanoramaTweaker.config.startingHorizontalAngle = val)).children())) {
            this.addDrawableChild(child);
        }

        y = ((this.height - widgetPadding) * 2) / widgetCount + y_padding;
        for (ClickableWidget child : ((new OptionWidget(this.textRenderer,
                Text.translatable("panorama_tweaker.verticalAngle"), x, y, 308, 20, Config.DEFAULT_VERTICAL_ANGLE,
                PanoramaTweaker.config.verticalAngle, -90, 90, val -> PanoramaTweaker.config.verticalAngle = val))
                        .children())) {
            this.addDrawableChild(child);
        }

        y = ((this.height - widgetPadding) * 3) / widgetCount + y_padding;
        for (ClickableWidget child : ((new OptionWidget(this.textRenderer,
                Text.translatable("panorama_tweaker.swayAngle"), x, y, 308, 20, Config.DEFAULT_SWAY_ANGLE,
                PanoramaTweaker.config.swayAngle, -90, 90, val -> PanoramaTweaker.config.swayAngle = val))
                        .children())) {
            this.addDrawableChild(child);
        }

        y = ((this.height - widgetPadding) * 4) / widgetCount + y_padding;
        for (ClickableWidget child : ((new OptionWidget(this.textRenderer,
                Text.translatable("panorama_tweaker.swaySpeed"), x, y, 308, 20, Config.DEFAULT_SWAY_SPEED,
                PanoramaTweaker.config.swaySpeed, 0, 10, val -> PanoramaTweaker.config.swaySpeed = val)).children())) {
            this.addDrawableChild(child);
        }

        y = ((this.height - widgetPadding) * 5) / widgetCount + y_padding;
        for (ClickableWidget child : ((new OptionWidget(this.textRenderer,
                Text.translatable("panorama_tweaker.initialSwayProgress"), x, y, 308, 20,
                Config.DEFAULT_INITIAL_SWAY_PROGRESS, PanoramaTweaker.config.initialSwayProgress, -1, 1,
                val -> PanoramaTweaker.config.initialSwayProgress = val)).children())) {
            this.addDrawableChild(child);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.backgroundRenderer.render(delta, MathHelper.clamp(1, 0.0F, 1.0F));
        for (Element child : this.children()) {
            if (child instanceof Drawable) {
                ((Drawable) child).render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : this.children()) {
            if (child instanceof ClickableWidget) {
                if (((ClickableWidget) child).isFocused()) {
                    child.setFocused(false);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void restoreConfig() {
        PanoramaTweaker.config = this.backupConfig;
    }

    protected void saveConfig() {
        PanoramaTweaker.config.save();
    }

    protected static class OptionWidget {
        private final double defaultVal;

        private final List<ClickableWidget> children;
        private final OptionSlider slider;
        private final TextFieldWidget textField;
        private final ButtonWidget resetButton;

        private final Consumer<Float> setter;

        private double value;

        public OptionWidget(TextRenderer font, Text text, int x, int y, int width, int height,
                double defaultVal, double progress, double scaledMin, double scaledMax, Consumer<Float> setter) {
            this.defaultVal = defaultVal;
            this.value = progress;

            this.setter = setter;

            this.slider = new OptionSlider(
                text, x, y, width - 110, height, progress, scaledMin, scaledMax, this::setValue
            );
            this.textField = new TextFieldWidget(font, x + width - 105, y, 50, height, text);
            this.textField.setText("" + (Math.round(this.value * 100) / 100d));
            this.textField.setEditable(true);
            this.textField.setChangedListener((val) -> {
                try {
                    double d = Double.parseDouble(val);
                    this.textField.setEditableColor(0xE0E0E0);
                    this.textField.setUneditableColor(0x707070);
                    if (this.value != d) {
                        this.setValue(d);
                    }
                } catch (NumberFormatException e) {
                    this.textField.setEditableColor(0xFF0000);
                    this.textField.setUneditableColor(0xFF0000);
                }
            });
            this.resetButton = ButtonWidget.builder(Text.translatable("controls.reset"),
                    (buttonWidget) -> this.resetToDefault()).position(x + width - 50, y).size(50, height).build();
            this.children = new ArrayList<>();
            this.children.add(this.slider);
            this.children.add(this.textField);
            this.children.add(this.resetButton);

            this.updateWidgets();
        }

        protected void setValue(double val) {
            this.value = val;
            this.setter.accept((float) val);
            this.updateWidgets();
        }

        protected void updateWidgets() {
            this.slider.updateValue(this.value);
            if (!this.textField.isFocused()) {
                this.textField.setText("" + (Math.round(this.value * 100) / 100d));
            }
            this.resetButton.active = !this.isDefault();
        }

        protected void resetToDefault() {
            this.setValue(this.defaultVal);
        }

        protected boolean isDefault() {
            return this.value == this.defaultVal;
        }

        public List<ClickableWidget> children() {
            return this.children;
        }

        protected static class OptionSlider extends SliderWidget {
            protected final Text text;
            protected final double scaledMin;
            protected final double scaledMax;
            protected final double scaledRange;
            protected final Consumer<Double> onUpdate;
            protected double scaledValue;

            public OptionSlider(Text text, int x, int y, int width, int height, double progress,
                    double scaledMin, double scaledMax, Consumer<Double> onUpdate) {
                super(x, y, width, height, Text.empty(),
                        MathHelper.clamp(((progress - scaledMin) / (scaledMax - scaledMin)), 0, 1));
                this.text = text;
                this.scaledMin = scaledMin;
                this.scaledMax = scaledMax;
                this.scaledRange = scaledMax - scaledMin;
                this.scaledValue = progress;
                this.onUpdate = onUpdate;
                this.updateMessage();
            }

            public void updateValue(double val) {
                this.scaledValue = val;
                this.value = MathHelper.clamp(((val - scaledMin) / scaledRange), 0, 1);
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(this.text.copy().append(": " + (Math.round(this.scaledValue * 10) / 10d)));
            }

            @Override
            protected void applyValue() {
                this.scaledValue = (this.value * scaledRange) + scaledMin;
                this.onUpdate.accept(this.scaledValue);
            }
        }
    }
}