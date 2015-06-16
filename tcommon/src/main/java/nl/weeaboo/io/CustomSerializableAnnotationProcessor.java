package nl.weeaboo.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("nl.weeaboo.io.CustomSerializable")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CustomSerializableAnnotationProcessor extends AbstractProcessor {

	private Messager messager;
    private Types typeUtils;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
	    super.init(env);

		messager = env.getMessager();
        typeUtils = env.getTypeUtils();
	}

	@Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
		for (Element e : env.getRootElements()) {
            if (e.getKind() == ElementKind.CLASS && e.getAnnotation(CustomSerializable.class) != null) {
				processClass((TypeElement)e);
			}
		}
        return true;
	}

    private void processClass(TypeElement classElement) {
        List<ExecutableElement> methods = ElementFilter.methodsIn(classElement.getEnclosedElements());

        if (!checkReadObject(methods)) {
            messager.printMessage(Diagnostic.Kind.WARNING, "Invalid readObject implementation",
                    classElement);
        }

        if (!checkWriteObject(methods)) {
            messager.printMessage(Diagnostic.Kind.WARNING, "Invalid writeObject implementation",
                    classElement);
        }
	}

    private boolean checkReadObject(List<ExecutableElement> methods) {
        // private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException;
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PRIVATE);
        List<String> paramTypes = types(ObjectInputStream.class);
        List<String> thrownTypes = types(IOException.class, ClassNotFoundException.class);
        return checkMethod(methods, modifiers, "readObject", paramTypes, thrownTypes);
    }

    private boolean checkWriteObject(List<ExecutableElement> methods) {
        // private void writeObject(ObjectOutputStream stream) throws IOException;
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PRIVATE);
        List<String> paramTypes = types(ObjectOutputStream.class);
        List<String> thrownTypes = types(IOException.class);
        return checkMethod(methods, modifiers, "writeObject", paramTypes, thrownTypes);
    }

    private boolean checkMethod(Collection<ExecutableElement> methods, Set<Modifier> modifiers,
            String name, List<String> paramTypes, List<String> exceptionTypes) {

        for (ExecutableElement method : methods) {
            if (!method.getSimpleName().contentEquals(name)) {
                continue; // This is not the method we're looking for
            }
            if (!methodMatches(method, modifiers, paramTypes, exceptionTypes)) {
                return false;
            }
        }
        return true;
    }

    private boolean methodMatches(ExecutableElement method, Set<Modifier> modifiers,
            List<String> paramTypes, List<String> exceptionTypes) {

        // Check modifier
        if (!method.getModifiers().equals(modifiers)) {
            return false;
        }

        // Check return type
        if (!typeUtils.isSameType(method.getReturnType(), typeUtils.getNoType(TypeKind.VOID))) {
            return false;
        }

        // Check parameter types
        List<? extends VariableElement> params = method.getParameters();
        if (params.size() != paramTypes.size()) {
            return false;
        }
        for (int n = 0; n < params.size(); n++) {
            TypeElement typeElement = (TypeElement) typeUtils.asElement(params.get(n).asType());
            String expectedType = paramTypes.get(n);
            if (!typeElement.getQualifiedName().contentEquals(expectedType)) {
                return false;
            }
        }

        // Check exception types
        Set<String> actualThrown = new HashSet<String>();
        for (TypeMirror thrownType : method.getThrownTypes()) {
            TypeElement typeElement = (TypeElement) typeUtils.asElement(thrownType);
            actualThrown.add(typeElement.getQualifiedName().toString());
        }
        if (!actualThrown.equals(new HashSet<String>(exceptionTypes))) {
            return false;
        }

        return true;
    }

    private static List<String> types(Class<?>... types) {
        List<String> names = new ArrayList<String>();
        for (Class<?> type : types) {
            names.add(type.getName());
        }
        return names;
    }

}
