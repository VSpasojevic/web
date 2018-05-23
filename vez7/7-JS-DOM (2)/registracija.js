



function validacija(forma){

	
	


	let imeEL = document.getElementsByName('ime')[0];
	let prezimeEL = document.getElementsByName('prezime')[0];
	let jmbgEL = document.getElementsByName('jmbg')[0];

	let ime= imeEL.value;
	let prezime= prezimeEL.value;
	let jmbg= jmbgEL.value;


	if (!ime){

		imeEL.style.background = 'red';
		return false;

	}

	if (!prezime){

		prezimeEL.style.background = 'red';
		return false;

	}
	if (!jmbg){

		jmbgEL.style.background = 'red';
		return false;

	}

/*


	if (!ime || !prezime || !jmbg){
		
		imeEL.style.background = 'red';
		prezimeEL.style.background = 'red';
		jmbgEL.style.background = 'red';
		
		return false;
	}
*/
	return true;
}
