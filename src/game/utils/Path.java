package game.utils;

public class Path {
    public static abstract class Flow {
        private String path;

        public Flow(String path) {
            this.path = path;
        }

        public Flow(Flow flow, String path) {
            this.path = flow.path + path;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    public static class Resources extends Flow {
        private Resources() {
            super("/resources");
        }
    }

    public static class Img extends Flow {
        private Img() {
            super(new Resources(), "/img");
        }

        //存角色素材
        public static class Actors extends Flow {
            private Actors(Flow flow) {
                super(flow, "/actors");
            }

            public String people1() {
                return this + "/people1.png";
            }

            public static class Walk extends Flow {
                private Walk(Flow flow) {
                    super(flow, "/walk");
                }

                public String Beige_walk1() {
                    return this + "/alienBeige_walk1.png";
                }

                public String Beige_walk2() {
                    return this + "/alienBeige_walk2.png";
                }

                public String Beige_stand() {
                    return this + "/alienBeige_stand.png";
                }

                public String Blue_walk1() {
                    return this + "/alienBlue_walk1.png";
                }

                public String Blue_walk2() {
                    return this + "/alienBlue_walk2.png";
                }

                public String Blue_stand() {
                    return this + "/alienBlue_stand.png";
                }

                public String Green_walk1() {
                    return this + "/alienGreen_walk1.png";
                }

                public String Green_walk2() {
                    return this + "/alienGreen_walk2.png";
                }

                public String Green_stand() {
                    return this + "/alienGreen_stand.png";
                }

                public String Pink_walk1() {
                    return this + "/alienPink_walk1.png";
                }

                public String Pink_walk2() {
                    return this + "/alienPink_walk2.png";
                }

                public String Pink_stand() {
                    return this + "/alienPink_stand.png";
                }

                public String Yellow_walk1() {
                    return this + "/alienYellow_walk1.png";
                }

                public String Yellow_walk2() {
                    return this + "/alienYellow_walk2.png";
                }

                public String Yellow_stand() {
                    return this + "/alienYellow_stand.png";
                }
            }

            public static class Back extends Flow {
                private Back(Flow flow) {
                    super(flow, "/back");
                }

                public String Beige_climb1() {
                    return this + "/alienBeige_climb1.png";
                }

                public String Beige_climb2() {
                    return this + "/alienBeige_climb2.png";
                }

                public String Blue_climb1() {
                    return this + "/alienBlue_climb1.png";
                }

                public String Blue_climb2() {
                    return this + "/alienBlue_climb2.png";
                }

                public String Green_climb1() {
                    return this + "/alienGreen_climb1.png";
                }

                public String Green_climb2() {
                    return this + "/alienGreen_climb2.png";
                }

                public String Pink_climb1() {
                    return this + "/alienPink_climb1.png";
                }

                public String Pink_climb2() {
                    return this + "/alienPink_climb2.png";
                }

                public String Yellow_climb1() {
                    return this + "/alienYellow_climb1.png";
                }

                public String Yellow_climb2() {
                    return this + "/alienYellow_climb2.png";
                }

            }

            public static class Bump extends Flow {
                private Bump(Flow flow) {
                    super(flow, "/bump");
                }

                public String Bump1() {
                    return this + "/tile_0004.png";
                }

                public String Bump2() {
                    return this + "/tile_0005.png";
                }

                public String Bump3() {
                    return this + "/tile_0006.png";
                }

                public String Bump4() {
                    return this + "/tile_0007.png";
                }

                public String Bump5() {
                    return this + "/tile_0008.png";
                }

                public String Bump6() {
                    return this + "/tile_0009.png";
                }
            }

            public Walk walk() {
                return new Walk(this);
            }

            public Back back() {
                return new Back(this);
            }

            public Bump bump() {
                return new Bump(this);
            }

        }

        //存第二層地圖素材：動物類型（可變身）
        public static class Animals extends Flow {
            private Animals(Flow flow) {
                super(flow, "/animals");
            }

            public String goat() {
                return this + "/goat.png";
            }
        }

        //存第一層地圖素材（不可變身）
        public static class Background extends Flow {
            private Background(Flow flow) {
                super(flow, "/backgrounds");
            }

            public String forest() {
                return this + "/forest.jpeg";
            }

            public String winter() {
                return this + "/winter.jpeg";
            }

            public String volcano() {
                return this + "/volcano.jpg";
            }

            public String village() {
                return this + "/village.jpg";
            }
            //森林地圖物件
            public String tree1() {
                return this + "/tree1.png";
            }

            //冰原地圖物件
            public String winterTree1() {
                return this + "/winterTree1.png";
            }

            //火山地圖物件
            public String rock() {
                return this + "/rock.png";
            }

            //村莊地圖物件
            public String house1() {
                return this + "/house1.png";
            }

            public String house2() {
                return this + "/house2.png";
            }

        }

        //只存地圖編輯器產生的檔案
        public static class Map extends Flow {
            private Map(Flow flow) {
                super(flow, "/map");
            }

            public String bmp() {
                return this + "/genMap.bmp";
            }

            public String txt() {
                return this + "/genMap.txt";
            }

        }

        public static class Menu extends Flow {
            private Menu(Flow flow) {
                super(flow, "/Menu");
            }

            public static class Button extends Flow {
                private Button(Flow flow) {
                    super(flow, "/Button");
                }

                public String button() {
                    return this + "/buttonword.png";
                }

                public String button1() {
                    return this + "/buttonword1.png";
                }

                public String button2() {
                    return this + "/buttonword2.png";
                }

                public String button3() {
                    return this + "/buttonword3.png";
                }

                public String button4() {
                    return this + "/buttonword4.png";
                }

                public String mouse() {
                    return this + "/mouse.png";
                }

                public String magicWand() {
                    return this + "/magicWand.png";
                }
            }

            public static class Scene extends Flow {
                private Scene(Flow flow) {
                    super(flow, "/Scene");
                }

                public String scene1() {
                    return this + "/scene1.png";
                }

                public String scene2() {
                    return this + "/scene2.png";
                }

                public String scene3() {
                    return this + "/scene3.png";
                }

                public String scene4() {
                    return this + "/scene4.png";
                }

                public String scene5() {
                    return this + "/scene5.png";
                }

                public String scene6() {
                    return this + "/scene6.png";
                }

                public String scene7() {
                    return this + "/scene7.png";
                }

                public String scene8() {
                    return this + "/scene8.png";
                }

                public String scene9() {
                    return this + "/scene9.png";
                }

                public static class Elements extends Flow {
                    private Elements(Flow flow) {
                        super(flow, "/Elements");
                    }

                    public String element1() {
                        return this + "/cloudLayer1.png";
                    }

                    public String element2() {
                        return this + "/cloudLayer2.png";
                    }
                }

                public Elements Elements() {
                    return new Elements(this);
                }
            }

            public Button Button() {
                return new Button(this);
            }

            public Scene Scene() {
                return new Scene(this);
            }

        }

        public static class Objs extends Flow {
            private Objs(Flow flow) {
                super(flow, "/objs");
            }

            public static class Move extends Flow {
                private Move(Flow flow) {
                    super(flow, "/move");
                }

                public String barnacle1() {
                    return this + "/barnacle.png";
                }

                public String barnacle2() {
                    return this + "/barnacle_bite.png";
                }

                public String bat1() {
                    return this + "/bat.png";
                }

                public String bat2() {
                    return this + "/bat_fly.png";
                }

                public String bee1() {
                    return this + "/bee.png";
                }
                public String bee2() {
                    return this + "/bee_fly.png";
                }

                public String bunny1() {
                    return this + "/bunny1_walk1.png";
                }
                public String bunny2() {
                    return this + "/bunny1_walk2.png";
                }

                public String bunny21() {
                    return this + "/bunny2_walk1.png";
                }
                public String bunny22() {
                    return this + "/bunny2_walk2.png";
                }

                public String fishGreen1() {
                    return this + "/fishGreen.png";
                }
                public String fishGreen2() {
                    return this + "/fishGreen_swim.png";
                }

                public String fishPink1() {
                    return this + "/fishPink.png";
                }
                public String fishPink2() {
                    return this + "/fishPink_swim.png";
                }

                public String fly1() {
                    return this + "/fly.png";
                }
                public String fly2() {
                    return this + "/fly_fly.png";
                }

                public String flyMan1() {
                    return this + "/flyMan_stand.png";
                }
                public String flyMan2() {
                    return this + "/flyMan_still_fly.png";
                }
                public String flyMan3() {
                    return this + "/flyMan_fly.png";
                }
                public String flyMan4() {
                    return this + "/flyMan_jump.png";
                }
                public String flyMan5() {
                    return this + "/flyMan_still_stand.png";
                }
                public String flyMan6() {
                    return this + "/flyMan_still_jump.png";
                }

                public String frog1() {
                    return this + "/frog.png";
                }
                public String frog2() {
                    return this + "/frog_leap.png";
                }

                public String ladyBug1() {
                    return this + "/ladyBug.png";
                }
                public String ladyBug2() {
                    return this + "/ladyBug_fly.png";
                }

                public String mouse1() {
                    return this + "/mouse.png";
                }
                public String mouse2() {
                    return this + "/mouse_walk.png";
                }

                public String slime1() {
                    return this + "/slime.png";
                }
                public String slime2() {
                    return this + "/slime_squashed.png";
                }

                public String slimeBlue1() {
                    return this + "/slimeBlue.png";
                }
                public String slimeBlue2() {
                    return this + "/slimeBlue_squashed.png";
                }

                public String slimeGreen1() {
                    return this + "/slimeGreen.png";
                }
                public String slimeGreen2() {
                    return this + "/slimeGreen_squashed.png";
                }

                public String snail1() {
                    return this + "/snail.png";
                }
                public String snail2() {
                    return this + "/snail_walk.png";
                }

                public String snake1() {
                    return this + "/snake.png";
                }
                public String snake2() {
                    return this + "/snake_walk.png";
                }

                public String snakeLava1() {
                    return this + "/snakeLava.png";
                }
                public String snakeLava2() {
                    return this + "/snakeLava_ani.png";
                }

                public String snakeSlime1() {
                    return this + "/snakeSlime.png";
                }
                public String snakeSlime2() {
                    return this + "/snakeSlime_ani.png";
                }

                public String spider1() {
                    return this + "/spider_walk1.png";
                }
                public String spider2() {
                    return this + "/spider_walk2.png";
                }

            }

            public String addSpeed() {
                return this + "/addSpeed.png";
            }
            public String warningLabel() {
                return this + "/warningLabel.png";
            }
            public Move move() {
                return new Move(this);
            }
            public DontMove dontMove() {
                return new DontMove(this);
            }
        }

        public static class DontMove extends Flow {
            private DontMove(Flow flow) {
                super(flow, "/dontmove");
            }
            public String question1() {
                return this + "/questionbox1.png";
            }
            public String question2() {
                return this + "/questionbox2.png";
            }
            public String question3() {
                return this + "/questionbox3.png";
            }
            public String runnerLight() {
                return this + "/runnerlight.png";
            }
            public String runnerDark() {
                return this + "/runnerdark.png";
            }
            public String runnernormal() {
                return this + "/runnernormal.png";
            }
            public String changeBody() {
                return this + "/changeRoleButton.png";
            }
            public String nothing() {
                return this + "/nothing.png";
            }

        }

        public Actors actors() {
            return new Actors(this);
        }
        public Animals animals() {
            return new Animals(this);
        }
        public Background background() {
            return new Background(this);
        }
        public Map map() {
            return new Map(this);
        }
        public Menu menu() {
            return new Menu(this);
        }
        public Objs objs() {
            return new Objs(this);
        }
    }

    public static class Sound extends Flow {
        private Sound() {
            super(new Resources(), "/sounds");
        }
    }

    public Img img() {
        return new Img();
    }

    public Sound sound() {
        return new Sound();
    }
}
