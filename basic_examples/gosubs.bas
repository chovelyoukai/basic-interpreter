:main
	print "number to raise: "
	input A
	print "power to raise it to: "
	input B
	gosub :pow

	print A
	print " raised to the "
	print B
	print " power is: "
	println Z
	end

# accepts input in variables A and B, returns in variable Z
:pow
	let aTemp A
	let bTemp B
:pow_loop
	if bTemp < 2 goto :pow_end
	let aTemp aTemp * A
	let bTemp bTemp - 1
	goto :pow_loop
:pow_end
	let Z aTemp
	return

	
