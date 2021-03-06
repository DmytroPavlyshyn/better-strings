package com.antkorwin.betterstrings;

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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.antkorwin.betterstrings.ast.InnerStringVarsAstTranslator;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

/**
 * Created on 2019-09-02
 * <p>
 * String interpolation annotation processor
 *
 * @author Korovin Anatoliy
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BetterStringsProcessor extends AbstractProcessor {

	private JavacProcessingEnvironment env;
	private Messager messager;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		messager = processingEnv.getMessager();
		env = (JavacProcessingEnvironment) processingEnv;
		super.init(processingEnv);
		printBanner();
	}


	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		if (roundEnv.processingOver()) {
			return false;
		}

		Context context = env.getContext();
		Trees trees = Trees.instance(env);

		for (Element codeElement : roundEnv.getRootElements()) {
			if (!isClassOrEnum(codeElement)) {
				continue;
			}
			JCTree tree = (JCTree) trees.getPath(codeElement).getCompilationUnit();
			new InnerStringVarsAstTranslator(context).translate(tree);
		}

		return false;
	}

	private boolean isClassOrEnum(Element codeElement) {
		return codeElement.getKind() == ElementKind.CLASS ||
		       codeElement.getKind() == ElementKind.ENUM;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	private void printBanner() {
		String banner = "v0.3 String Interpolation Java Plugin, by Anatoliy Korovin";
		messager.printMessage(Diagnostic.Kind.NOTE, banner);
	}
}
