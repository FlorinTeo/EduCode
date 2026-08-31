package edu.ftdev.Maze.helpers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class WClass {
    protected static String _packageName;

    protected String _className;
    protected Class _wrapC;
    protected Map<String, Constructor> _wrapCtors;
    protected Map<String, Method> _wrapMs;
    protected Object _wrapObj;
    
    public static void setup(Class<?> testClass) {
        String testPackage = testClass.getPackageName();
        if (!testPackage.endsWith(".tests")) {
            fail(String.format("### Invalid test package '%s'. Expected package ending in '.tests'.", testPackage));
        }
        _packageName = testPackage.substring(0, testPackage.length() - ".tests".length()) + ".main";
    }

    public static String pkgFix(String s) {
        return s.replaceAll("#([^#]+)#", _packageName + ".$1");
    }

	private void loadClass() {
        _wrapCtors = new HashMap<String, Constructor>();
        for (Constructor ctor : _wrapC.getConstructors()) {
            _wrapCtors.put(ctor.toString(), ctor);
        }
        _wrapMs = new HashMap<String, Method>();
        for (Method m : _wrapC.getMethods()) {
            _wrapMs.put(m.toString(), m);
        }
        for (Method m : _wrapC.getDeclaredMethods()) {
            if (!_wrapMs.containsKey(m.toString())) {
                _wrapMs.put(m.toString(), m);
                m.trySetAccessible();
            }
        }
        Class superClass = _wrapC.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            for (Method m : superClass.getDeclaredMethods()) {
                if (!_wrapMs.containsKey(m.toString())) {
                    _wrapMs.put(m.toString(), m);
                    m.trySetAccessible();
                }
            }
        }
    }
    
    //#region: Constructor/instantiate/newInstance sequence
    public WClass() {
        String thisClassName = this.getClass().getName();
        _className = thisClassName.replace("edu.ftdev.Maze.helpers", _packageName);
        try {
            _wrapC = Class.forName(_className);
            loadClass();
            _wrapObj = null;
        } catch (Exception e) {
            fail(String.format("### Missing or invalid '%s' class definition.", _className));
        }
    }
    
    public WClass(Object o) {
        _className = o.getClass().getName();
        _wrapC = o.getClass();
        loadClass();
        _wrapObj = o;
    }
    
    protected abstract void instantiate() throws Exception;
    
    public WClass newInstance() {
        try {
            instantiate();
        } catch (Exception e) {
            fail(String.format("### Missing or invalid '%s' constructor.", _className));
        }
        return this;
    }
    //#endregion: Constructor/instantiate/newInstance sequence
    
    public Object getInstance() {
        return _wrapObj;
    }
    
    protected Object invoke(String mName, Object... params) {
        Object result = null;
        Method mGetRow = getMethod(mName);
        try {
            result = mGetRow.invoke(_wrapObj, params);
        } catch (Exception e) {
            fail(String.format("### Runtime error invoking '%s'.", mGetRow.getName()));
        }
        return result;
    }
    
    public static void checkChain(String[] chain) {
        for (int i = 0; i < chain.length-1; i++) {
            Class c1 = null;
            try {
                c1 = Class.forName(chain[i]);
            } catch(ClassNotFoundException e) {
                fail(String.format("### Missing or invalid '%s' class definition." ,  chain[i]));
            }
            
            Class c2 = null;
            try {
                c2 = Class.forName(chain[i+1]);
            } catch(ClassNotFoundException e) {
                fail(String.format("### Missing or invalid '%s' class definition." ,  chain[i+1]));
            }
            
            assertTrue(
                    c1.getSuperclass().equals(c2),
                    String.format("Invalid class hierarchy: '%s' is not a subclass of '%s'.",
                            chain[i], chain[i+1]));
        }
    }
    
    private static boolean checkDeclMatch(String pattern, String decl) {
        String[] parts = pattern.split("\\*");
        boolean matched = true;
        int idx = 0;
        for (int i = 0; matched && i < parts.length; i++) {
            if (!parts[i].trim().isEmpty()) {
                idx = decl.indexOf(parts[i], idx);
                matched = (idx != -1);
            }
        }
        return matched;
    }
    
    private static void checkAndMark(String actMethod, Map<String, Boolean> expMethods) {
        for(String expMethod : expMethods.keySet()) {
            if (!expMethods.get(expMethod)) {
                if (checkDeclMatch(expMethod, actMethod)) {
                    expMethods.replace(expMethod, true);
                    break;
                }
            }
        }
    }
    
    public static void checkClass(String className, String... decls) {
        // adjust className and decls
        className = pkgFix(className);
         for (int i = 0; i < decls.length; i++) {
            decls[i] = pkgFix(decls[i]);
        }
        try {
            String[] chain = className.split(" extends ");
            for (int i = 0; i < chain.length; i++) {
                if (!chain[i].contains(".")) {
                    chain[i] = _packageName + "." + chain[i];
                }
            }
            className = String.join(" extends ", chain);
            checkChain(chain);
            Class thisC = Class.forName(chain[0]);
            Class superC = thisC.getSuperclass();
            
            if (decls.length > 0) {
                Map<String, Boolean> mMap = new HashMap<String, Boolean>();
                for (String d : decls) {
                    mMap.put(d, false);
                }

                for (Constructor c : thisC.getConstructors()) {
                    mMap.replace(c.toString(), true);
                }
                
                for (Method m : thisC.getDeclaredMethods()) {
                    checkAndMark(m.toString(), mMap);
                }
                
                for (Method m : superC.getDeclaredMethods()) {
                    checkAndMark(m.toString(), mMap);
                }
                
                for (Map.Entry<String, Boolean > kvp : mMap.entrySet()) {
                    assertTrue(
                            kvp.getValue(),
                            String.format("### Missing or invalid '%s' method declaration in class '%s' ###.", kvp.getKey(), className));
                }
            }
        } catch(ClassNotFoundException e) {
            fail(String.format("### Missing or invalid '%s' class definition." ,  className));
        }
    }
    
    public Constructor getCtor(String cDecl) {
        assertTrue(
                _wrapCtors.containsKey(cDecl),
                String.format("### Missing or invalid '%s' constructor declaration in class '%s'.", cDecl, _wrapC.getName()));
        return _wrapCtors.get(cDecl);
    }
    
    public Method getMethod(String mPattern) {
        String mDecl = null;
        for (String m : _wrapMs.keySet()) {
            if (checkDeclMatch(mPattern, m)) {
                mDecl = m;
                break;
            }
        }
        assertTrue(
                mDecl != null,
                String.format("### Missing or invalid '%s' method declaration in class '%s'.", mPattern, _wrapC.getName()));
        return _wrapMs.get(mDecl);
    }
}
