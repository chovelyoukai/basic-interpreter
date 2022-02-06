println "I'll pick a random number between 1 and 100, and you have to guess what it is!"
let target (rnd 98) + 1
:guess
	print "what is your guess? "
	input guess
	if guess = target goto :win
	if guess < target goto :less
	if guess > target goto :more

:win
	println "Congratulations! You guessed the number!"
	end

:less
	println "Nope, the answer is higher than that."
	goto :guess

:more
	println "Nope, the answer is lower than that."
	goto :guess
