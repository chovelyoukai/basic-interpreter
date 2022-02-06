let i 0
:loop1
	let a (i + 1) * 2
	put a i
	let i i + 1
	if i >= 4096 goto :skip1
	goto :loop1

:skip1
	let i 0
:loop2
	get a i
	print a
	print " "
	let i i + 1
	if i >= 4096 goto :skip2
	goto :loop2

:skip2
	println ""
	end
