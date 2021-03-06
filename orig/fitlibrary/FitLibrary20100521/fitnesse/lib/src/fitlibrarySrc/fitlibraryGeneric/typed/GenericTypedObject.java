/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 22/10/2006
*/

package fitlibraryGeneric.typed;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.Closure;
import fitlibrary.closure.LookupClosure;
import fitlibrary.global.PlugBoard;
import fitlibrary.parser.Parser;
import fitlibrary.parser.lookup.FieldParser;
import fitlibrary.parser.lookup.GetterParser;
import fitlibrary.parser.lookup.ResultParser;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.traverse.DomainAdapter;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.RuntimeContextual;
import fitlibrary.traverse.workflow.caller.ValidCall;
import fitlibrary.typed.Typed;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibrary.utility.option.None;
import fitlibrary.utility.option.Option;
import fitlibrary.utility.option.Some;

public class GenericTypedObject implements TypedObject {
	public final static GenericTypedObject NULL = new GenericTypedObject(null);
	private final GenericTyped typed;
	private final Object subject;
	private final LookupClosure lookupClosure;
	private final MethodTargetFactory methodTargetFactory;

	public GenericTypedObject(Object subject, GenericTyped typed) {
		this(subject,typed,PlugBoard.lookupClosure, new MethodTargetFactory(){
			@Override
			public CalledMethodTarget createCalledMethodTarget(Closure closure, Evaluator evaluator) {
				return new CalledMethodTarget(closure,evaluator);
			}});
	}
	public GenericTypedObject(Object subject) {
		this(subject,typeOf(subject));
	}
	public GenericTypedObject(Object subject, LookupClosure lookupClosure, MethodTargetFactory methodTargetFactory) { // Allows test injection
		this(subject,typeOf(subject),lookupClosure,methodTargetFactory);
	}
	protected GenericTypedObject(Object subject, GenericTyped typed, LookupClosure lookupClosure, MethodTargetFactory methodTargetFactory) { // Allows test injection
		this.subject = subject;
		this.lookupClosure = lookupClosure;
		this.methodTargetFactory = methodTargetFactory;
		this.typed = typed;
	}
	public interface MethodTargetFactory {
		CalledMethodTarget createCalledMethodTarget(Closure closure, Evaluator evaluator);
	}
	private static GenericTyped typeOf(Object subject) {
		if (subject == null)
			return new GenericTyped(void.class);
		return new GenericTyped(subject.getClass());
	}
	public Object getSubject() {
		return subject;
	}
	protected TypedObject asTypedObject(Object sut) {
		return new GenericTypedObject(sut);
	}
	@Override
	public Class<?> classType() {
		return subject.getClass();
	}
	protected Typed resultTyped(Method method) {
		Type genericReturnType = typed.bind(method.getGenericReturnType(),describe(method));
		return new GenericTyped(genericReturnType,true);
	}
	protected Typed resultTyped(Field field) {
		Type genericReturnType = typed.bind(field.getGenericType(),describe(field));
		return new GenericTyped(genericReturnType,true);
	}
	public ResultParser resultParser(Evaluator evaluator, Method method) {
		Typed resultTyped = resultTyped(method);
		return new GetterParser(getTyped().on(evaluator, resultTyped, true),
				method); // This doesn't handle String result case
	}
	public ResultParser resultParser(Evaluator evaluator, Field field) {
		Typed resultTyped = resultTyped(field);
		return new FieldParser(getTyped().on(evaluator, resultTyped, true),
				field); // This doesn't handle String result case
	}
	@Override
	public GetterParser resultParser(Evaluator evaluator, Method method, Class<?> actualResultType) {
		Typed resultTyped = new GenericTyped(actualResultType,true);
		return new GetterParser(typed.on(evaluator,resultTyped,true),method);
	}
	@Override
	public ResultParser resultParser(Evaluator evaluator, Field field, Class<?> actualResultType) {
		Typed resultTyped = new GenericTyped(actualResultType,true);
		return new FieldParser(typed.on(evaluator,resultTyped,true),field);
	}
	protected Typed parameterTyped(Method method, int parameterNo) {
		Type givenType = method.getGenericParameterTypes()[parameterNo];
		Type genericParameterType = typed.bind(givenType,describe(method));
		return new GenericTyped(genericParameterType,true);
	}
	@Override
	public TypedObject asReturnTypedObject(Object object, Method method) {
		return new GenericTypedObject(object,
				new GenericTyped(typed.bind(method.getGenericReturnType(),describe(method))));
	}
	@Override
	public TypedObject asReturnTypedObject(Object object, Field field) {
		return new GenericTypedObject(object,
				new GenericTyped(typed.bind(field.getGenericType(),describe(field))));
	}
	public Parser[] parameterParsers(Evaluator evaluator, Method method) {
		Class<?>[] types = method.getParameterTypes();
		Parser[] parameterParsers = new Parser[types.length];
		for (int i = 0; i < types.length; i++) {
			Typed parameterTyped = parameterTyped(method, i);
			parameterParsers[i] = getTyped().on(evaluator, parameterTyped,
					false);
		}
		return parameterParsers;
	}
	public Evaluator traverse(Evaluator evaluator) {
		return getTyped().parser(evaluator).traverse(this);
	}
	public void findMethodsFromPlainText(String textCall, List<ValidCall> results) {
		List<String> words = Arrays.asList(textCall.split(" "));
		Method[] methods = subject.getClass().getMethods();
		for (Method method : methods) {
			int argCount = method.getParameterTypes().length;
			if (method.getDeclaringClass() != Object.class
					&& !PlugBoard.lookupClosure.fitLibrarySystemMethod(method,argCount, subject)) {
				ValidCall.parseAction(words, method.getName(), argCount,results);
			}
		}
	}
	public Closure findPublicMethodClosureForTypedObject(String name, Class<?>[] argTypes) {
		return PlugBoard.lookupClosure.findPublicMethodClosure(this, name, argTypes);
	}
	public Option<CalledMethodTarget> new_findSpecificMethod(String name, int argCount, Evaluator evaluator) {
		Option<Closure> methodClosureOption = new_findMethodClosure(name, argCount);
		if (methodClosureOption.isSome())
			return new Some<CalledMethodTarget>(methodTargetFactory.createCalledMethodTarget(methodClosureOption.get(), evaluator));
		return None.none();
	}
	private Option<Closure> new_findMethodClosure(String name, int argCount) {
		Closure methodClosure = lookupClosure.findMethodClosure(this,name, argCount);
		if (methodClosure == null)
			return None.none();
		return new Some<Closure>(methodClosure);
	}
	public CalledMethodTarget new_optionallyFindGetterOnTypedObject(String propertyName, Evaluator evaluator) {
		String getMethodName = ExtendedCamelCase.camel("get " + propertyName);
		Option<CalledMethodTarget> target = new_findSpecificMethod(getMethodName, 0, evaluator);
		if (target.isSome())
			return target.get();
		String isMethodName = ExtendedCamelCase.camel("is " + propertyName);
		target = new_findSpecificMethod(isMethodName, 0, evaluator);
		if (target.isSome())
			return target.get();
		return null;
	}
	@Override
	public String toString() {
		return "GenericTypedObject["+subject+":"+typed+"]";
	}
	@Override
	public Typed getTyped() {
		return typed;
	}
	@Override
	public boolean isNull() {
		return subject == null;
	}
	private String describe(Method method) {
		return "in "+method.toGenericString();
	}
	private String describe(Field field) {
		return "in "+field.getName();
	}
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof GenericTypedObject))
			return false;
		if (subject == null)
			return ((GenericTypedObject)arg).subject == null;
		return subject.equals(((GenericTypedObject)arg).subject);
	}
	@Override
	public int hashCode() {
		if (subject == null)
			return -123;
		return subject.hashCode();
	}
	@Override
	public TypedObject getTypedSystemUnderTest() {
		if (subject instanceof Evaluator)
			return ((Evaluator)subject).getTypedSystemUnderTest();
		if (subject instanceof DomainAdapter)
			return new GenericTypedObject(((DomainAdapter)subject).getSystemUnderTest());
		throw new RuntimeException("No SUT");
	}
	@Override
	public boolean hasTypedSystemUnderTest() {
		return subject instanceof DomainAdapter && ((DomainAdapter)subject).getSystemUnderTest() != null;
	}
	@Override
	public void injectRuntime(RuntimeContextInternal runtime) {
		if (subject instanceof RuntimeContextual)
			((RuntimeContextual)subject).setRuntimeContext(runtime);
		if (hasTypedSystemUnderTest())
			getTypedSystemUnderTest().injectRuntime(runtime);
	}
}
