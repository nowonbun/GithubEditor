package common.IF;

public interface LambdaExpression<T, R> {
	R run(T node);
}