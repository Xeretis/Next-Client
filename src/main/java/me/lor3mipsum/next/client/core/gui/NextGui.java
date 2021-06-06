package me.lor3mipsum.next.client.core.gui;

import com.lukflug.panelstudio.base.*;
import com.lukflug.panelstudio.component.*;
import com.lukflug.panelstudio.container.IContainer;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.*;
import com.lukflug.panelstudio.mc16fabric.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.CenteredPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.popup.PanelPositioner;
import com.lukflug.panelstudio.popup.PopupTuple;
import com.lukflug.panelstudio.setting.*;
import com.lukflug.panelstudio.theme.IColorScheme;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.theme.ThemeTuple;
import com.lukflug.panelstudio.widget.ITextFieldKeys;
import com.lukflug.panelstudio.widget.ToggleSwitch;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.render.font.FontUtils;
import me.lor3mipsum.next.client.core.gui.components.NextColorPickerComponent;
import me.lor3mipsum.next.client.core.gui.themes.NextTheme;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.HudModule;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;
import me.lor3mipsum.next.client.impl.settings.*;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class NextGui extends MinecraftHUDGUI {
    public static final int WIDTH = 120, HEIGHT = 12, FONT_HEIGHT = 9, DISTANCE = 10, HUD_BORDER = 2;

    public static IClient client;
    public static GUIInterface guiInterface;
    public static HUDGUI gui;

    public NextGui() {
        ClickGuiModule clickGuiModule = Main.moduleManager.getModule(ClickGuiModule.class);

        guiInterface = new GUIInterface(true) {
            @Override
            public void drawString(Point pos, int height, String s, Color c) {
                end(false);
                FontUtils.drawString( s, pos.x, pos.y, c);
                begin(false);
            }

            @Override
            public int getFontWidth(int height, String s) {
                return (int) FontUtils.getStringWidth(s);
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

        ITheme theme = new NextTheme(new NextColorScheme(true), FONT_HEIGHT, 3, 5, ": " + Formatting.GRAY);

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
                        Stream<ISetting<?>> temp=Main.settingManager.getAllSettingsFrom(module).stream().map(NextGui.this::createSetting);
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
                if (guiToggle.isOn() && super.isOn()) return false;
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

        IntFunction<IResizable> resizable =  width -> new IResizable() {
            final Dimension size = new Dimension(width, 320);

            @Override
            public Dimension getSize() {
                return new Dimension(size);
            }

            @Override
            public void setSize(Dimension size) {
                this.size.width = size.width;
                this.size.height = size.height;
                if (size.width < 75) this.size.width = 75;
                if (size.height<50) this.size.height = 50;
            }
        };

        //HUD
        for (Module module : Main.moduleManager.getModules()) {
            if (module instanceof HudModule) {
                ((HudModule)module).populate(theme);
                gui.addHUDComponent(((HudModule) module).getComponent(), new IToggleable() {
                    @Override
                    public void toggle() {
                        module.toggle();
                    }

                    @Override
                    public boolean isOn() {
                        return module.getEnabled();
                    }
                }, animation.get(), theme, HUD_BORDER);
            }
        }


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
        }, false, () -> clickGuiModule.layout.getValue() == ClickGuiModule.Layout.Normal, title -> title) {
            @Override
            protected IResizable getResizable (int width) {
                return resizable.apply(width);
            }

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

        ITextFieldKeys textFieldKeys = new ITextFieldKeys() {

            @Override
            public boolean isBackspaceKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_BACKSPACE;
            }

            @Override
            public boolean isDeleteKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_DELETE;
            }

            @Override
            public boolean isInsertKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_INSERT;
            }

            @Override
            public boolean isLeftKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_LEFT;
            }

            @Override
            public boolean isRightKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_RIGHT;
            }

            @Override
            public boolean isHomeKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_HOME || scancode == GLFW.GLFW_KEY_KP_7;
            }

            @Override
            public boolean isEndKey(int scancode) {
                return scancode == GLFW.GLFW_KEY_END || scancode == GLFW.GLFW_KEY_KP_1;
            }

            @Override
            public boolean isCopyKey(int scancode) {
                return false;
            }

            @Override
            public boolean isPasteKey(int scancode) {
                return false;
            }

            @Override
            public boolean isCutKey(int scancode) {
                return false;
            }

            @Override
            public boolean isAllKey(int scancode) {
                return false;
            }
        };

        IComponentGenerator generator=new ComponentGenerator(scancode -> scancode == GLFW.GLFW_KEY_DELETE, scancode -> true, textFieldKeys) {
            @Override
            public IComponent getBooleanComponent (IBooleanSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ToggleSwitch(setting,theme.getToggleSwitchRenderer(isContainer));
            }

            @Override
            public IComponent getColorComponent (IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new NextColorPickerComponent(setting, new ThemeTuple(theme.theme,theme.logicalLevel,colorLevel));
            }

        };

        //Panel Layout
        ILayout classicPanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + WIDTH)/2, HEIGHT+DISTANCE,animation,level-> ChildUtil.ChildMode.DOWN, level-> ChildUtil.ChildMode.DOWN,popupType);

        classicPanelLayout.populateGUI(classicPanelAdder,generator,client, theme);

        //CSGO Layout
        PopupTuple csgoPopup=new PopupTuple(new CenteredPositioner(()->new Rectangle(new Point(0,0),guiInterface.getWindowSize())),true,new IScrollSize() {});

        IComponentAdder horizontalCSGOAdder=new PanelAdder(gui,true,()-> clickGuiModule.layout.getValue() == ClickGuiModule.Layout.CSGO, title->title) {
            @Override
            protected IResizable getResizable (int width) {
                return resizable.apply(width);
            }
        };

        ILayout horizontalCSGOLayout=new CSGOLayout(new Labeled("Next Client",null,()->true),new Point(100,100),480,WIDTH,animation,"Enabled",true,true,2, ChildUtil.ChildMode.DOWN,csgoPopup) {
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

        horizontalCSGOLayout.populateGUI(horizontalCSGOAdder,generator,client, theme);

        //Searchable layout
        IComponentAdder searchableAdder =new PanelAdder(gui,true,()-> clickGuiModule.layout.getValue() == ClickGuiModule.Layout.Searchable, title->title) {
            @Override
            protected IResizable getResizable (int width) {
                return resizable.apply(width);
            }
        };

        ILayout searchableLayout=new SearchableLayout(new Labeled("Next Client",null,()->true), new Labeled("Search", null, ()->true), new Point(100,100),480, WIDTH, animation,"Enabled",2, ChildUtil.ChildMode.DOWN, csgoPopup, Comparator.comparing(ILabeled::getDisplayName), a -> true, textFieldKeys) {
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

        searchableLayout.populateGUI(searchableAdder,generator,client, theme);
    }

    private ISetting<?> createSetting (Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            return new IBooleanSetting() {
                @Override
                public String getDisplayName() {
                    return "> " + setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return setting::getVisible;
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
                }
            };
        } else if (setting instanceof IntegerSetting) {
            return new INumberSetting() {
                @Override
                public String getDisplayName() {
                    return "> " +  setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return setting::getVisible;
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
                    return "> " +  setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return setting::getVisible;
                }

                @Override
                public double getNumber() {
                    return ((Number) setting.getValue()).doubleValue();
                }

                @Override
                public void setNumber(double value) {
                    ((DoubleSetting)setting).setValue((double) Math.round(value * Math.pow(10, 1)) / Math.pow(10, 1));
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
        } else if (setting instanceof EnumSetting) {
            return new IEnumSetting() {
                private final ILabeled[] states=((EnumSetting<Enum<?>>)setting).getModes().stream().map(mode->new Labeled(mode,null,()->true)).toArray(ILabeled[]::new);

                @Override
                public String getDisplayName() {
                    return "> " +  setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return setting::getVisible;
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
                }
            };
        } else if (setting instanceof ColorSetting) {
            return new IColorSetting() {
                @Override
                public String getDisplayName() {
                    return "> " + setting.getName();
                }

                @Override
                public IBoolean isVisible() {
                    return setting::getVisible;
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
                    return true;
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
                    return setting::getVisible;
                }

                @Override
                public void setValue(String string) {
                    ((StringSetting) setting).setValue(string);
                }

                @Override
                public String getDisplayName() {
                    return "> " +  setting.getName();
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
                    return "> " +  setting.getName();
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
                return setting::getVisible;
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
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return ((Number) Main.moduleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue()).intValue();
    }

    @Override
    protected HUDGUI getGUI() {
        return gui;
    }

    private static final class NextColorScheme implements IColorScheme {

        private final boolean isVisible;

        public NextColorScheme (boolean isVisible) {
            this.isVisible=isVisible;
        }

        @Override
        public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
            Main.moduleManager.getModule(ClickGuiModule.class).registerColor(name, rainbow, new NextColor(color), isVisible, allowsRainbow, hasAlpha);
        }

        @Override
        public Color getColor(String name) {
            return ((ColorSetting)Main.settingManager.getAllSettingsFrom(Main.moduleManager.getModule(ClickGuiModule.class)).stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null)).getValue();
        }
    }
}
