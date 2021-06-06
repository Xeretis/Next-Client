package me.lor3mipsum.next.client.core.gui.components;

import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ColorPicker;
import com.lukflug.panelstudio.widget.NumberSlider;
import com.lukflug.panelstudio.widget.ToggleSwitch;

public class NextColorPickerComponent extends NextColorComponent {

    public NextColorPickerComponent (IColorSetting setting, ThemeTuple theme) {
        super(setting,theme);
    }

    @Override
    public void populate (ThemeTuple theme) {
        addComponent(new ColorPicker(setting,theme.theme.getColorPickerRenderer()));
        addComponent(new NumberSlider(new ColorNumber(0,()->true),theme.getSliderRenderer(false)));
        addComponent(new NumberSlider(new ColorNumber(3,()->true),theme.getSliderRenderer(false)));
        addComponent(new ToggleSwitch(new Labeled("  - Rainbow", null, setting::allowsRainbow), new IToggleable() {
            @Override
            public void toggle() {
                setting.setRainbow(!setting.getRainbow());
            }

            @Override
            public boolean isOn() {
                return setting.getRainbow();
            }
        }, theme.theme.getToggleSwitchRenderer(theme.logicalLevel, theme.graphicalLevel+1, false)));
    }

}
