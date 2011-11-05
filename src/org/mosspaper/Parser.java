package org.mosspaper;

import org.mosspaper.objects.AbsMossObject;
import org.mosspaper.objects.MossObject;
import org.mosspaper.objects.DataProvider;
import org.mosspaper.objects.TextObjects;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Parser {

    public class NewLine implements MossObject {

        public void preDraw(Env env) { }

        public void draw(Env env) {
            if (env.getLineHeight() <= 0) {
                env.setY(env.getY() + env.getPaint().getTextSize() + PADDING);
            } else {
                env.setY(env.getY() + env.getLineHeight() + PADDING);
            }
            env.setX(0.0f);
            env.resetLineHeight();
        }

        public void postDraw(Env env) {
        }

        public DataProvider getDataProvider() {
            return null;
        }
    }

    public class Text extends AbsMossObject implements MossObject {

        @Override
        public String toString() {
            return value;
        }

        private String value;
    }

    public Parser() {
        this.errors = new ArrayList<MossException>();
        this.lineNo = 1;
        this.colNo = 1;
    }

    public void buildEnv(Env env, InputStream is) throws IOException {
        String s = Common.slurp(is);
        String[] split = s.split("TEXT");
        if (split.length == 2) {
            env.setConfig(parseConfig(env, split[0]));
            env.setLayout(parseLayout(env, split[1]));
        } else {
            env.setLayout(parseLayout(env, split[0]));
        }
    }

    private Config parseConfig(Env env, String s) {
        /* State: 0 = key, 1 = values */
        int state = 0;
        Config config = new Config();
        StringBuffer key = new StringBuffer("");
        StringBuffer value = new StringBuffer("");
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            colNo++;
            if (state == 0) {
                if (Character.isWhitespace(c)) {
                    if ('\n' == c) {
                        lineNo++;
                    }
                    if (key.length() > 0) {
                        state = 1;
                    }
                } else {
                    key.append(c);
                }
            } else {
                if ('\n' == c) {
                    try {
                        config.put(
                            env,
                            key.toString().trim(),
                            value.toString().trim());
                    } catch (MossException e) {
                        e.setIdent(key.toString().trim());
                        e.setLineNo(lineNo);
                        e.setColNo(colNo);
                        env.addExs(e);
                    }
                    lineNo++;
                    colNo = 1;
                    state = 0;
                    key = new StringBuffer("");
                    value = new StringBuffer("");
                } else {
                    value.append(c);
                }
            }
        }
        return config;
    }

    private Layout parseLayout(Env env, String s) {
        Layout layout = new Layout();
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ('$' == c) {
                if (buf.length() > 0) {
                    Text t = new Text();
                    t.value = buf.toString();
                    layout.add(t);
                    buf = new StringBuffer("");
                }
                i = parseIdent(env, s, i + 1, layout);
                continue;
            } else if ('\n' == c) {
                lineNo++;
                colNo = 1;
                if (buf.length() > 0) {
                    Text t = new Text();
                    t.value = buf.toString();
                    layout.add(t);
                    buf = new StringBuffer("");
                }
                layout.add(new NewLine());
            } else {
                buf.append(c);
            }
        }
        return layout;
    }

    private int parseIdent(Env env, String s, int pos, Layout layout) {
        int i = pos;
        StringBuffer varName = new StringBuffer("");
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            colNo++;
            if ('{' == c) {
                return parseFunc(env, s, i + 1, layout);
            } else if (!isAlpha(c)) {
                try {
                    MossObject obj = TextObjects.inst(varName.toString(), new ArrayList<Object>(0));
                    if (null != obj) {
                        if (null != layout) {
                            layout.add(obj);
                        } else {
                            mossArg = obj;
                        }
                    }
                } catch (MossException e) {
                    e.setLineNo(lineNo);
                    e.setColNo(colNo);
                    env.addExs(e);
                }
                return i - 1;
            } else {
                varName.append(c);
            }
        }
        return i;
    }

    private int parseFunc(Env env, String s, int pos, Layout layout) {
        int i = pos;
        int state = 0;
        StringBuffer funcName = new StringBuffer("");
        StringBuffer arg = new StringBuffer("");
        List<Object> args = new ArrayList<Object>();
        for (; i < s.length(); ++i) {
            char c = s.charAt(i);
            colNo++;
            if ('$' == c) {
                i = parseIdent(env, s, i + 1, null);
                args.add(mossArg);
                mossArg = null;
            } else if ('"' == c) {
                i = parseString(env, s, i + 1, arg);
            } else if (Character.isWhitespace(c)) {
                if (funcName.length() > 0) {
                    state = 1;
                }
                if (state == 1 && arg.length() > 0) {
                    args.add(arg.toString());
                    arg = new StringBuffer("");
                }
            } else if ('}' == c) {
                if (arg.length() > 0) {
                    args.add(arg.toString());
                }
                try {
                    MossObject obj = TextObjects.inst(funcName.toString(), args);
                    if (null != obj) {
                        if (null != layout) {
                            layout.add(obj);
                        } else {
                            mossArg = obj;
                        }
                    }
                } catch (MossException e) {
                    e.setLineNo(lineNo);
                    e.setColNo(colNo);
                    env.addExs(e);
                }
                return i;
            } else {
                if (state == 0) {
                    funcName.append(c);
                } else {
                    arg.append(c);
                }
            }
        }
        return i;
    }

    private int parseString(Env env, String s, int pos, StringBuffer str) {
        int i = pos;
        for (; i < s.length(); ++i) {
            char c = s.charAt(i);
            if ('"' == c) {
                return i;
            } else {
                str.append(c);
            }
        }
        return i;
    }

    private boolean isAlpha(char c) {
        return Character.isLetterOrDigit(c) || '_' == c;
    }

    private MossObject mossArg;
    private List<MossException> errors;
    private int lineNo;
    private int colNo;

    static final float PADDING = 2.0f;
    static final String TAG = "MossParser";
}
