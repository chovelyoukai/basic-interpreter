let counter 0
:loop
	print "hi "
	println counter + 1
	sleep 50
	let counter counter + 1
	if counter = 10 end
	goto :loop
