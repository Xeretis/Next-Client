package me.lor3mipsum.next.client.gui.clickgui;

import com.lukflug.panelstudio.*;
import com.lukflug.panelstudio.hud.HUDClickGUI;
import com.lukflug.panelstudio.hud.HUDPanel;
import com.lukflug.panelstudio.mc16.GLInterface;
import com.lukflug.panelstudio.mc16.MinecraftHUDGUI;
import com.lukflug.panelstudio.settings.*;
import com.lukflug.panelstudio.theme.*;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.gui.clickgui.components.DynamicDescription;
import me.lor3mipsum.next.client.gui.clickgui.components.ResetableKeybindComponent;
import me.lor3mipsum.next.client.gui.clickgui.components.SyncableColorComponent;
import me.lor3mipsum.next.client.gui.clickgui.themes.NextTheme;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;
import me.lor3mipsum.next.client.impl.modules.client.ColorMode;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.setting.Setting;
import me.lor3mipsum.next.client.setting.SettingManager;
import me.lor3mipsum.next.client.utils.FontUtils;
import java.awt.*;

public class NextGui extends MinecraftHUDGUI {
    public final static int WIDTH=100,HEIGHT=12,DISTANCE=6,HUD_BORDER=2;
    private final ColorScheme scheme = new SettingsColorScheme(ClickGuiModule.INSTANCE.activeColor, ClickGuiModule.INSTANCE.backgroundColor, ClickGuiModule.INSTANCE.settingBackgroundColor, ClickGuiModule.INSTANCE.outlineColor, ClickGuiModule.INSTANCE.fontColor, ClickGuiModule.INSTANCE.opacity);
    private final Theme theme, nextLineTheme, nextOutlineTheme, nextOutlineLineTheme, nextTheme, gameSenseTheme, clearTheme, clearGradientTheme;
    private final Toggleable colorToggle;
    public final GUIInterface guiInterface;
    public final HUDClickGUI gui;

    public NextGui() {
        nextLineTheme = new NextTheme(scheme, HEIGHT,true, false);
        nextTheme = new NextTheme(scheme, HEIGHT, false, false);
        nextOutlineTheme = new NextTheme(scheme, HEIGHT, false, true);
        nextOutlineLineTheme = new NextTheme(scheme, HEIGHT, true, true);
        gameSenseTheme = new GameSenseTheme(scheme, HEIGHT, 2, (int) ClickGuiModule.INSTANCE.scrollSpeed.getNumber());
        clearTheme = new ClearTheme(scheme, false, HEIGHT, 1);
        clearGradientTheme = new ClearTheme(scheme, true, HEIGHT, 1);
        theme = new ThemeMultiplexer() {
            @Override
            protected Theme getTheme() {
                if (ClickGuiModule.INSTANCE.theme.is("Next") && ClickGuiModule.INSTANCE.line.isOn() && !ClickGuiModule.INSTANCE.outline.isOn()) return nextLineTheme;
                else if (ClickGuiModule.INSTANCE.theme.is("Next") && ClickGuiModule.INSTANCE.line.isOn() && ClickGuiModule.INSTANCE.outline.isOn()) return nextOutlineLineTheme;
                else if (ClickGuiModule.INSTANCE.theme.is("Next") && !ClickGuiModule.INSTANCE.line.isOn() && ClickGuiModule.INSTANCE.outline.isOn()) return nextOutlineTheme;
                else if (ClickGuiModule.INSTANCE.theme.is("Next") && !ClickGuiModule.INSTANCE.line.isOn() && !ClickGuiModule.INSTANCE.outline.isOn()) return nextTheme;
                else if (ClickGuiModule.INSTANCE.theme.is("GameSense")) return  gameSenseTheme;
                else if (ClickGuiModule.INSTANCE.theme.is("Clear")) return clearTheme;
                else return clearGradientTheme;
            }
        };
        colorToggle = new Toggleable() {
            @Override
            public void toggle() {
                ColorMode.colorModel.increment();
            }

            @Override
            public boolean isOn() {
                return ColorMode.colorModel.is("HSB");
            }
        };
        guiInterface = new GUIInterface(true) {
            @Override
            protected String getResourcePrefix() {
                return "next:textures/";
            }

            @Override
            public void drawString(Point pos, String s, Color c) {
                GLInterface.end();
                int x=pos.x+2, y=pos.y+1;
                FontUtils.drawStringWithShadow(s, x, y, c);
                GLInterface.begin();
            }

            @Override
            public int getFontWidth(String s) {
                return Math.round(FontUtils.getStringWidth(s))+4;
            }

            @Override
            public int getFontHeight() {
                return Math.round(FontUtils.getFontHeight())+2;
            }
        };
        gui = new HUDClickGUI(guiInterface, new DynamicDescription(new Point(5, 0), new Point(0, 0))) {
            @Override
            public void handleScroll (int diff) {
                super.handleScroll(diff);
                if (ClickGuiModule.INSTANCE.scrollMode.is("Screen")) {
                    for (FixedComponent component: components) {
                        if (!hudComponents.contains(component)) {
                            Point p=component.getPosition(guiInterface);
                            p.translate(0,-diff);
                            component.setPosition(guiInterface,p);
                        }
                    }
                }
            }
        };
        Toggleable hudToggle = new Toggleable() {
            @Override
            public void toggle() {
                render();
            }

            @Override
            public boolean isOn() {
                return hudEditor;
            }
        };

        for (Module module : Next.INSTANCE.moduleManager.getModules()) {
            if (module instanceof HudModule) {
                ((HudModule) module).populate(theme);
                gui.addHUDComponent(new HUDPanel(((HudModule)module).getComponent(), theme.getPanelRenderer(), module, new SettingsAnimation(ClickGuiModule.INSTANCE.animationSpeed), hudToggle, HUD_BORDER));
            }
        }

        Point pos = new Point(DISTANCE, DISTANCE);
        for (Category category : Category.values()) {
            DraggableContainer panel = new DraggableContainer(category.toString(), null, theme.getPanelRenderer(), new SimpleToggleable(false), new SettingsAnimation(ClickGuiModule.INSTANCE.animationSpeed), null, new Point(pos), WIDTH) {
                @Override
                protected int getScrollHeight (int childHeight) {
                    if (ClickGuiModule.INSTANCE.scrollMode.is("Screen")) {
                        return childHeight;
                    }
                    return Math.min(childHeight,Math.max(HEIGHT*4,NextGui.this.height-getPosition(guiInterface).y-renderer.getHeight(open.getValue()!=0)-HEIGHT));
                }
            };
            gui.addComponent(panel);
            pos.translate( WIDTH + DISTANCE, 0);
            for (Module module : Next.INSTANCE.moduleManager.getModulesByCategory(category)) {
                addModule(panel, module);
            }
        }
    }

    private void addModule(CollapsibleContainer panel, Module module) {
        CollapsibleContainer container=new CollapsibleContainer(module.getName(),module.getDescription(),theme.getContainerRenderer(),new SimpleToggleable(false),new SettingsAnimation(ClickGuiModule.INSTANCE.animationSpeed), module);

        if (!module.isHidden()) {
            panel.addComponent(container);
            for (Setting setting : SettingManager.getAllSettingsFrom(module.getName())) {
                if (setting instanceof BooleanSetting) {
                    container.addComponent(new BooleanComponent(setting.getName(),null,theme.getComponentRenderer(),(BooleanSetting) setting));
                } else if (setting instanceof NumberSetting) {
                    container.addComponent(new NumberComponent(setting.getName(),null,theme.getComponentRenderer(),(NumberSetting) setting,((NumberSetting) setting).getMinimumValue(),((NumberSetting) setting).getMaximumValue()));
                }  else if (setting instanceof ModeSetting) {
                    container.addComponent(new EnumComponent(setting.getName(),null,theme.getComponentRenderer(),(ModeSetting) setting));
                }	else if (setting instanceof ColorSetting) {
                    container.addComponent(new SyncableColorComponent(theme, (me.lor3mipsum.next.client.impl.settings.ColorSetting) setting,colorToggle,new SettingsAnimation(ClickGuiModule.INSTANCE.animationSpeed)));
                } else if (setting instanceof KeybindSetting) {
                    container.addComponent(new ResetableKeybindComponent(theme.getComponentRenderer(),(KeybindSetting) setting));
                }
            }
        }
    }

    @Override
    protected HUDClickGUI getHUDGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return (int) ClickGuiModule.INSTANCE.scrollSpeed.getNumber();
    }
}
