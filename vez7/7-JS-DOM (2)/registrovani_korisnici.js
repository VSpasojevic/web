


	window.onload = function(event){

		let users= [

			{'ime': 'Petar','prezime': 'Petrovic','jmbg':'123321'},
			{'ime': 'Stevan','prezime': 'Stevanovic','jmbg':'111111'},
			{'ime': 'Marko','prezime': 'Markovic','jmbg':'123456'},
			{'ime': 'Petar','prezime': 'Petrovic','jmbg':'123331'}
		];


		let tabela = document.getElementsByTagName('table')[0];

		for(user of users){

			let userTr = document.createElement('tr');
			let imeTd = document.createElement('td');
			let prezimeTd = document.createElement('td');
			let jmbgTd = document.createElement('td');
	


			//dodavanje teksta u svako polje

			imeTd.appendChild(document.createTextNode(user.ime));
			prezimeTd.appendChild(document.createTextNode(user.prezime));
			jmbgTd.appendChild(document.createTextNode(user.jmbg));


			//dodavanje polja u red 

			userTr.appendChild(imeTd);
			userTr.appendChild(prezimeTd);
			userTr.appendChild(jmbgTd);


			//dodavanje reda u tabelu

			tabela.appendChild(userTr);

		}


	};	