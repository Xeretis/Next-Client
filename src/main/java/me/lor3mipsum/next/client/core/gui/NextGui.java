package me.lor3mipsum.next.client.core.gui;

import com.lukflug.panelstudio.base.*;
import com.lukflug.panelstudio.component.*;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.*;
import com.lukflug.panelstudio.mc16fabric.GLInterface;
import com.lukflug.panelstudio.mc16fabric.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.widget.ColorComponent;
import com.lukflug.panelstudio.widget.ToggleButton;
import com.lukflug.panelstudio.widget.ToggleSwitch;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.gui.components.NextColorComponent;
import me.lor3mipsum.next.client.core.gui.themes.NextTheme;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.ModuleManager;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;
import me.lor3mipsum.next.client.impl.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class NextGui extends MinecraftHUDGUI {
    public static final int WIDTH = 100, HEIGHT = 12, FONT_HEIGHT = 9, DISTANCE = 10, HUD_BORDER = 2;

    public static IClient client;
    public static GUIInterface guiInterface;
    public static HUDGUI gui;
    private final ITheme theme;

    public NextGui() {
        ClickGuiModule clickGuiModule = Main.moduleManager.getModule(ClickGuiModule.class);

        guiInterface = new GUIInterface(true) {
            @Override
            public void drawString(Point pos, int height, String s, Color c) {
                end(false);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(new MatrixStack(), s,pos.x,pos.y,c.getRGB());
                begin(false);
            }

            @Override
            public int getFontWidth(int height, String s) {
                return MinecraftClient.getInstance().textRenderer.getWidth(s);
            }

            @Override
            public double getScreenWidth() {
                return super.getScreenWidth();
            }

            @Override
            public double getScreenHeight() {
                return super.getScreenHeight();
            }

            @Override
            protected String getResourcePrefix() {
                return "next:textures";
            }
        };

        theme = new NextTheme(new NextColorScheme(true),FONT_HEIGHT,3,5,": " + Formatting.GRAY);

        for(Setting s : Main.settingManager.getAllSettingsFrom(clickGuiModule.getClass()))
            System.out.println(s.getName());
        System.out.println(Main.moduleManager.getModulesInCategory(Category.CLIENT));

        client = () -> Arrays.stream(Category.values()).sorted(Comparator.comparing(Category::toString)).map(category -> new ICategory() {
            @Override
            public String getDisplayName() {
                return category.toString();
            }

            @Override
            public Stream<IModule> getModules() {
                return Main.moduleManager.getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).map(module -> new IModule() {
                    @Override
                    public String getDisplayName() {
                        return module.getName();
                    }

                    @Override
                    public String getDescription() {
                        return module.getDescription();
                    }

                    @Override
                    public IToggleable isEnabled() {
                        return new IToggleable() {
                            @Override
                            public void toggle() {
                                module.toggle();
                            }

                            @Override
                            public boolean isOn() {
                                return module.getEnabled();
                            }
                        };
                    }

                    @Override
                    public Stream<ISetting<?>> getSettings() {
                        Stream<ISetting<?>> temp=Main.settingManager.getAllSettingsFrom(module.getClass()).stream().map(setting->createSetting(setting));
                        return Stream.concat(temp, Stream.concat(Stream.of(new IBooleanSetting() {
                            @Override
                            public void toggle() {
                                module.setDrawn(!module.getDrawn());
                            }

                            @Override
                            public boolean isOn() {
                                return module.getDrawn();
                            }

                            @Override
                            public String getDisplayName() {
                                return "Drawn";
                            }
                        }), Stream.of(new IKeybindSetting() {
                            @Override
                            public int getKey() {
                                return module.getBind();
                            }

                            @Override
                            public void setKey(int key) {
                                module.setBind(key);
                            }

                            @Override
                            public String getKeyName() {
                                return KeyboardUtils.getKeyName(module.getBind());
                            }

                            @Override
                            public String getDisplayName() {
                                return "KeyBind";
                            }
                        })));
                    }
                });
            }
        });

        IToggleable guiToggle = new SimpleToggleable(false);
        IToggleable hudToggle = new SimpleToggleable(false) {
            @Override
            public boolean isOn() {
                return super.isOn();
            }
        };
        gui = new HUDGUI(guiInterface, theme.getDescriptionRenderer(), new MousePositioner(new Point(10, 10)), guiToggle, hudToggle);
        BiFunction<Context, Integer, Integer> scrollHeight = ((context, componentHeight)  -> {
            if (clickGuiModule.scrollMode.getValue() == ClickGuiModule.ScrollMode.Screen) return componentHeight;
            else return Math.min(componentHeight,Math.max(HEIGHT*4,NextGui.this.height-context.getPos().y-HEIGHT));
        });
        Supplier<Animation> animation = () -> new SettingsAnimation(() -> clickGuiModule.animationSpeed.getValue(), () -> guiInterface.getTime());
        PopupTuple popupType = new PopupTuple(new PanelPositioner(new Point(0, 0)), false, new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return scrollHeight.apply(context, componentHeight);
            }
        });

        //HUD

        //GUI
        IComponentAdder classicPanelAdder = new PanelAdder(new IContainer<IFixedComponent>() {
            @Override
            public boolean addComponent(IFixedComponent component) {
                return gui.addComponent(new IFixedComponentProxy<IFixedComponent>() {
                    @Override
                    public void handleScroll (Context context, int diff) {
                        IFixedComponentProxy.super.handleScroll(context,diff);
                        if (clickGuiModule.scrollMode.getValue() == ClickGuiModule.ScrollMode.Screen) {
                            Point p = getPosition(guiInterface);
                            p.translate(0, -diff);
                            setPosition(guiInterface, p);
                        }
                    }

                    @Override
                    public IFixedComponent getComponent() {
                        return component;
                    }
                });
            }

            @Override
            public boolean addComponent(IFixedComponent component, IBoolean visible) {
                return gui.addComponent(new IFixedComponentProxy<IFixedComponent>() {
                    @Override
                    public void handleScroll (Context context, int diff) {
                        IFixedComponentProxy.super.handleScroll(context,diff);
                        if (clickGuiModule.scrollMode.getValue() == ClickGuiModule.ScrollMode.Screen) {
                            Point p = getPosition(guiInterface);
                            p.translate(0, -diff);
                            setPosition(guiInterface, p);
                        }
                    }

                    @Override
                    public IFixedComponent getComponent() {
                        return component;
                    }
                },visible);
            }

            @Override
            public boolean removeComponent(IFixedComponent component) {
                return gui.removeComponent(component);
            }
        }, false, () -> !clickGuiModule.csgoLayout.getValue(), title -> title) {
            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return new IScrollSize() {
                    @Override
                    public int getScrollHeight (Context context, int componentHeight) {
                        return scrollHeight.apply(context,componentHeight);
                    }
                };
            }
        };

        IComponentGenerator generator=new ComponentGenerator(scancode -> scancode == GLFW.GLFW_KEY_DELETE, scancode -> true, scancode -> scancode == GLFW.GLFW_KEY_BACKSPACE, scancode -> scancode == GLFW.GLFW_KEY_DELETE, scancode -> scancode == GLFW.GLFW_KEY_INSERT, scancode -> scancode == GLFW.GLFW_KEY_LEFT, scancode -> scancode == GLFW.GLFW_KEY_RIGHT, scancode -> scancode == GLFW.GLFW_KEY_HOME || scancode == GLFW.GLFW_KEY_KP_7, scancode -> scancode == GLFW.GLFW_KEY_END || scancode == GLFW.GLFW_KEY_KP_1) {
            @Override
            public IComponent getBooleanComponent (IBooleanSetting setting, Supplier<Animation> animation, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ToggleSwitch(setting,theme.getToggleSwitchRenderer(isContainer));
            }

            @Override
            public IComponent getColorComponent (IColorSetting setting, Supplier<Animation> animation, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new NextColorComponent((IColorSetting)setting,animation.get(),new ThemeTuple(theme.theme,theme.logicalLevel,colorLevel));
            }

        };
        ILayout classicPanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + WIDTH)/2, HEIGHT+DISTANCE,animation,level-> ChildUtil.ChildMode.DOWN, level-> ChildUtil.ChildMode.DOWN,popupType);
        classicPanelLayout.populateGUI(classicPanelAdder,generator,client,theme);

        PopupTuple colorPopup=new PopupTuple(new CenteredPositioner(()->new Rectangle(new Point(0,0),guiInterface.getWindowSize())),true,new IScrollSize() {});
        IComponentAdder horizontalCSGOAdder=new PanelAdder(gui,true,()->clickGuiModule.csgoLayout.getValue(),title->title);
        ILayout horizontalCSGOLayout=new CSGOLayout(new Labeled("Next Client",null,()->true),new Point(100,100),480,WIDTH,animation,"Enabled",true,true,2, ChildUtil.ChildMode.DOWN,colorPopup) {
            @Override
            public int getScrollHeight (Context context, int componentHeight) {
                return 320;
            }

            @Override
            protected boolean isUpKey (int key) {
                return key==GLFW.GLFW_KEY_UP;
            }

            @Override
            protected boolean isDownKey (int key) {
                return key==GLFW.GLFW_KEY_DOWN;
            }

            @Override
            protected boolean isLeftKey (int key) {
                return key==GLFW.GLFW_KEY_LEFT;
            }

            @Override
            protected boolean isRightKey (int key) {
                return key==GLFW.GLFW_KEY_RIGHT;
            }
        };
        horizontalCSGOLayout.populateGUI(horizontalCSGOAdder,generator,client,theme);
    }

    private ISetting<?> createSetting (Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            return new IBooleanSetting() {
                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return ()->setting.getVisible();
                }

                @Override
                public void toggle() {
                    ((BooleanSetting)setting).setValue(!((BooleanSetting)setting).getValue());
                }

                @Override
                public boolean isOn() {
                    return ((BooleanSetting)setting).getValue();
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    return null;
					/*if (setting.getSubSettings().count()==0) return null;
					return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
                }
            };
        } else if (setting instanceof IntegerSetting) {
            return new INumberSetting() {
                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return ()->setting.getVisible();
                }

                @Override
                public double getNumber() {
                    return ((Number) setting.getValue()).doubleValue();
                }

                @Override
                public void setNumber(double value) {
                    ((IntegerSetting)setting).setValue((int) Math.round(value));
                }

                @Override
                public double getMaximumValue() {
                    return ((Number) ((IntegerSetting) setting).getMax()).doubleValue();
                }

                @Override
                public double getMinimumValue() {
                    return ((Number) ((IntegerSetting) setting).getMin()).doubleValue();
                }

                @Override
                public int getPrecision() {
                    return 0;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    return null;
                }
            };
        } else if (setting instanceof DoubleSetting) {
            return new INumberSetting() {
                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return ()->setting.getVisible();
                }

                @Override
                public double getNumber() {
                    return ((Number) setting.getValue()).doubleValue();
                }

                @Override
                public void setNumber(double value) {
                    ((DoubleSetting)setting).setValue(value);
                }

                @Override
                public double getMaximumValue() {
                    return ((Number) ((DoubleSetting) setting).getMax()).doubleValue();
                }

                @Override
                public double getMinimumValue() {
                    return ((Number) ((DoubleSetting) setting).getMin()).doubleValue();
                }

                @Override
                public int getPrecision() {
                    return 1;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    return null;
                }
            };
        } else if (setting instanceof FloatSetting) {
            return new INumberSetting() {
                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return ()->setting.getVisible();
                }

                @Override
                public double getNumber() {
                    return ((Number) setting.getValue()).doubleValue();
                }

                @Override
                public void setNumber(double value) {
                    ((FloatSetting)setting).setValue((float) value);
                }

                @Override
                public double getMaximumValue() {
                    return ((Number) ((FloatSetting) setting).getMax()).doubleValue();
                }

                @Override
                public double getMinimumValue() {
                    return ((Number) ((FloatSetting) setting).getMin()).doubleValue();
                }

                @Override
                public int getPrecision() {
                    return 1;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    return null;
                }
            };
        } else if (setting instanceof EnumSetting) {
            return new IEnumSetting() {
                private final ILabeled[] states=((EnumSetting<Enum<?>>)setting).getModes().stream().map(mode->new Labeled(mode,null,()->true)).toArray(ILabeled[]::new);

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return ()->setting.getVisible();
                }

                @Override
                public void increment() {
                    ((EnumSetting<Enum<?>>)setting).increment();
                }

                @Override
                public void decrement() {
                    ((EnumSetting<Enum<?>>)setting).decrement();
                }

                @Override
                public String getValueName() {
                    return ((EnumSetting)setting).getValue().toString();
                }

                @Override
                public int getValueIndex() {
                    return ((EnumSetting<Enum<?>>)setting).getModes().indexOf(getValueName());
                }

                @Override
                public void setValueIndex(int index) {
                    ((EnumSetting)setting).setValue(((EnumSetting<Enum<?>>)setting).getModes().get(index));
                }

                @Override
                public ILabeled[] getAllowedValues() {
                    return states;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    return null;
					/*if (setting.getSubSettings().count()==0) return null;
					return setting.getSubSettings().map(subSetting->createSetting(subSetting));*/
                }
            };
        } else if (setting instanceof ColorSetting) {
            return new IColorSetting() {
                @Override
                public String getDisplayName() {
                    return Formatting.BOLD + setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return () -> setting.getVisible();
                }

                @Override
                public Color getValue() {
                    return ((ColorSetting) setting).getValue();
                }

                @Override
                public void setValue(Color value) {
                    ((ColorSetting) setting).setValue(new NextColor(value));
                }

                @Override
                public Color getColor() {
                    return ((ColorSetting) setting).getColor();
                }

                @Override
                public boolean getRainbow() {
                    return ((ColorSetting) setting).getRainbow();
                }

                @Override
                public void setRainbow(boolean rainbow) {
                    ((ColorSetting) setting).setRainbow(rainbow);
                }

                @Override
                public boolean hasAlpha() {
                    return ((ColorSetting) setting).getAlphaEnabled();
                }

                @Override
                public boolean allowsRainbow() {
                    return ((ColorSetting) setting).getRainbowEnabled();
                }

                @Override
                public boolean hasHSBModel() {
                    //TODO: Different types
                    return false;
                }

                @Override
                public Stream<ISetting<?>> getSubSettings() {
                    return null;
                }
            };
        }else if (setting instanceof StringSetting) {
            return new IStringSetting() {
                @Override
                public String getValue() {
                    return ((StringSetting) setting).getValue();
                }

                @Override
                public IBoolean isVisible() {
                    return ()->setting.getVisible();
                }

                @Override
                public void setValue(String string) {
                    ((StringSetting) setting).setValue(string);
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }
            };
        } else if (setting instanceof KeyBindSetting) {
            return new IKeybindSetting() {
                @Override
                public int getKey() {
                    return ((KeyBindSetting) setting).getValue();
                }

                @Override
                public void setKey(int key) {
                    ((KeyBindSetting) setting).setValue(key);
                }

                @Override
                public String getKeyName() {
                    return KeyboardUtils.getKeyName(((KeyBindSetting) setting).getValue());
                }

                @Override
                public String getDisplayName() {
                    return setting.getName();
                }
            };
        }
        return new ISetting<Void>() {
            @Override
            public String getDisplayName() {
                return setting.getName();
            }

            @Override
            public IBoolean isVisible() {
                return ()->setting.getVisible();
            }

            @Override
            public Void getSettingState() {
                return null;
            }

            @Override
            public Class<Void> getSettingClass() {
                return Void.class;
            }

            @Override
            public Stream<ISetting<?>> getSubSettings() {
                return null;
            }
        };
    }

    @Override
    protected HUDGUI getHUDGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return ((Number) Main.moduleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue()).intValue();
    }

    private final class NextColorScheme implements IColorScheme {

        private final boolean isVisible;

        public NextColorScheme (boolean isVisible) { ;
            this.isVisible=isVisible;
        }

        @Override
        public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
            Main.moduleManager.getModule(ClickGuiModule.class).registerColor(name, rainbow, new NextColor(color), isVisible, allowsRainbow, hasAlpha);
        }

        @Override
        public Color getColor(String name) {
            return ((ColorSetting)Main.settingManager.getAllSettingsFrom(ClickGuiModule.class).stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null)).getValue();
        }
    }
}
