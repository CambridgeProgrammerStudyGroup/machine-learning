import operator
import random

operators = {
	operator.add:"+", 
	operator.mul:"*", 
	operator.truediv:"/", 
	operator.sub:"-",
	operator.pow:"^"
}

def gen(depth):
	if depth < 0:
		return random.choice([random.randint(-10,10), "x"])
	else:
		return random.choice([
			random.randint(-10,10),
			"x",
			[random.choice(list(operators.keys())), gen(depth-1), gen(depth-1)]
		])

def evaluate(expression, env):
	if isinstance(expression, str):
		return env[expression]
	elif isinstance(expression, int):
		return expression
	else:
		return expression[0](evaluate(expression[1], env), evaluate(expression[2], env))

def toStr(expression):
	if isinstance(expression, str):
		return expression
	elif isinstance(expression, int):
		return str(expression)
	else:
		return "(" +operators[expression[0]]+" "+ toStr(expression[1]) +" "+ toStr(expression[2]) +")"



for e in [gen(5) for i in range(30)]:
	print("{} -> ".format(toStr(e)), end="", flush=True)
	try:
		result = evaluate(e, {"x":11})
	except Exception:
		result = "Exception"
	print(result)





