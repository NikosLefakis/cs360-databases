/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


/* global fetch */
function toggleRentVehicle(){
     var renting = document.getElementById("renting");

    if (renting.style.display === "none") {
        renting.style.display = "block";
    } else {
        renting.style.display = "none";
    }

}


/* Customer */
function toggleCustomer() {
    var customerRegister = document.getElementById("CustomerRegister");
    var renting = document.getElementById("renting");

    if (customerRegister.style.display === "none") {
        renting.style.display = "none";
        customerRegister.style.display = "block";
    } else {
        customerRegister.style.display = "none";
      
    }
}

function registerCustomer() {
    var dataObject = {
        firstname: document.getElementById('firstname').value,
        lastname: document.getElementById('lastname').value,
        birthdate: document.getElementById('birthdate').value,
        address: document.getElementById('address').value,
        card_number: document.getElementById('card_number').value,
        card_holder: document.getElementById('card_holder').value,
        exp_date: document.getElementById('exp_year').value + "-" + document.getElementById('exp_month').value,
        cvv: document.getElementById('cvv').value,
        licence_num: document.getElementById('licence_num').value
    };


    const fetchUrl = "http://localhost:8080/CS360/RegisterCustomer";

    fetch(fetchUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dataObject)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            alert("You successfully registered");
            console.log(data);
        })
        .catch(error => {
            alert(error);
            console.error('Error fetching rentable details:', error);
        });

}


   

// Function to check if the selected vehicle requires a license
function requiresLicense(vehicleType) {
    return vehicleType === 'car' || vehicleType === 'motorbike';
}

function rentVehicle() {
    const birthdateInput = document.getElementById('birthdate').value;
    const birthdateArray = birthdateInput.split('-');
    const birthdate = new Date(birthdateArray[0], birthdateArray[1] - 1, birthdateArray[2]);

    // Calculate age
    const today = new Date();
    const age = today.getFullYear() - birthdate.getFullYear();

    const selectedVehicle = document.querySelector('input[name="vehicle"]:checked').value;
    const licence_number = document.getElementById('licence_number').value;

    // Check age for motorbike
    if (age < 16 && selectedVehicle === 'motorbike') {
        alert('You must be 16 or older to rent a motorbike.');
    
    // Check age for car
    } else if (age < 18 && selectedVehicle === 'car') {
        alert('You must be 18 or older to rent a car.');
    } else {
        const renter_id = document.getElementById('renter_id').value;
        const driver_name = document.getElementById('driver_name').value;
        const from_date = document.getElementById('from_date').value;
        const to_date = document.getElementById('to_date').value;
        const payment_checkbox = document.getElementById('payment_checkbox').checked;
        const xhr = new XMLHttpRequest();
        const fetchUrl = `http://localhost:8080/CS360/SpecificTypeOfVehicle?type=${selectedVehicle}&from_date=${from_date}&to_date=${to_date}`;

        fetch(fetchUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log(data);

            // Check if the selected vehicle requires a license and if the license number is empty
            if (requiresLicense(selectedVehicle) && !licence_number.trim()) {
                alert('You must have a valid license to rent this vehicle.');
            } else {
                displayRentableDetails(data);
            }
        })
        .catch(error => {
            alert(error);
            console.error('Error fetching rentable details:', error);
        });
    }
}


// Function to display rentable details
function displayRentableDetails(data) {
    const resultContainer = document.getElementById('resultContainer');

    // Create a table element
    const table = document.createElement('table');
    table.border = '1';

    // Create table header
    const headerRow = table.insertRow(0);
    const headers = ['ID', 'Type', 'Brand', 'Model', 'Color', 'Autonomy', 'Car Type', 'Passengers', 'Rent Price', 'Insurance Price', 'Select'];
    headers.forEach(headerText => {
        const header = document.createElement('th');
        header.innerHTML = headerText;
        headerRow.appendChild(header);
    });

    // Populate the table with data
    data.forEach(item => {
        const row = table.insertRow();
        const cellProps = ['id', 'type', 'brand', 'model', 'color', 'autonomy', 'car_type', 'passengers', 'rent_price', 'insurance_price'];

        cellProps.forEach(prop => {
            const cell = row.insertCell();
            cell.innerHTML = item[prop];
        });

        // Create a button in the last column
        const selectButton = document.createElement('button');
        selectButton.innerText = 'Select';
        selectButton.onclick = function () {
            finishRenting(item.id,item.rent_price, item.insurance_price);
        };

        const cell = row.insertCell();
        cell.appendChild(selectButton);
    });

    // Clear previous content and append the table to the resultContainer
    resultContainer.innerHTML = '';
    resultContainer.appendChild(table);
}


function finishRenting(vehicle_id, rent_price, insurance_price) {
    const renter_id = document.getElementById('renter_id').value;
    const driver_name = document.getElementById('driver_name').value;
    const from_date_str = document.getElementById('from_date').value;
    const to_date_str = document.getElementById('to_date').value;
    const hasPaid = document.getElementById('payment_checkbox').checked ? 'yes' : 'no';
    const selectedVehicle = document.querySelector('input[name="vehicle"]:checked').value;

    const from_date = new Date(from_date_str);
    const to_date = new Date(to_date_str);
    const timeDiff = to_date.getTime() - from_date.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
    const rentCost = daysDiff * rent_price;
    const insuranceCost = daysDiff * insurance_price;
    const total_cost = rentCost + insuranceCost;
   
    const formattedFromDate = from_date.toISOString().split('T')[0];
    const formattedToDate = to_date.toISOString().split('T')[0];

    const dataObject = {
        renter_id: renter_id,
        driver_name: driver_name,
        from_date: formattedFromDate, 
        to_date: formattedToDate,
        total_cost: total_cost.toString(),
        hasPaid: hasPaid,
        vehicle_id: vehicle_id,
        type: selectedVehicle
    };

    const fetchUrl = "http://localhost:8080/CS360/Renting";

    fetch(fetchUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dataObject)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            alert("You successfully rented this car");
            console.log(data);
        })
        .catch(error => {
            alert(error);
            console.error('Error fetching rentable details:', error);
        });

}




function toggleDamageReport(){    
    const damagereport = document.getElementsByClassName('form-container-damage')[0];
    damagereport.style.display = "block";
    const rentableDetailsDiv = document.getElementById('RentableDetails');
    rentableDetailsDiv.innerHTML = ""; 
    
}

function submitDamageReportForm(event) {
    const damagereport = document.getElementsByClassName('form-container-damage')[0];
    damagereport.style.display = "";
    const name = document.getElementById('lastname').value;
    const vehicle_types = document.getElementById("type");
    const selectedOption = vehicle_types.options[vehicle_types.selectedIndex].text;
    const car_id = document.getElementById('car_id').value;
    var today = new Date(); // to find today's date
    const fromdate = new Date(today); // clone the current date
    const todate = new Date(today); // clone the current date
    const reason = "damage";
    fromdate.setDate(today.getDate());
    todate.setDate(today.getDate() + 3);

    fetch('http://localhost:8080/projectaki/replacevehicle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            type: selectedOption,
            car_id: car_id,
            reason: reason,
            name: name,
            selectedOption: selectedOption,
            fromdate: fromdate.toISOString().split('T')[0],
            todate: todate.toISOString().split('T')[0]
        })
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const responseData = data;
    })
    .catch(error => {
        console.error('Error:', error);
    });
}


function toggleFixable() {
     const damagereport = document.getElementsByClassName('form-container-damage')[0];
    damagereport.style.display = "none";
    fetch('http://localhost:8080/projectaki/showdamagedvehicles', {
        method: 'GET',
        headers: {
            'Content-Type': 'text/plain'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(data => {
        const rentableDetailsDiv = document.getElementById('RentableDetails');
        rentableDetailsDiv.innerHTML = data;
        addButtonsToRows();

    })
    .catch(error => {
        alert(error);
        console.error('Error:', error);
    });
}

function addButtonsToRows() {
    const tables = document.querySelectorAll('#RentableDetails table');
    tables.forEach(table => {
  
        const rows = table.querySelectorAll('tr');

        for (let i = 1; i < rows.length; i++) {
            const row = rows[i];

            // Create a button for each row
            const button = document.createElement('button');
            button.textContent = 'Fix/Service';
            const buttonText =  "Fix/Service";
            
                button.addEventListener('click', function() {
                      let row_data = Array.from(row.cells)
        .map(cell => (cell.textContent.trim() !== buttonText) ? cell.textContent.trim() : null) 
        .filter(cellContent => cellContent !== null);
                    ServiceOrFix(row_data);

                });

       
            button.style.marginLeft = '20px'; 

            const lastCell = row.cells[row.cells.length - 1];
            lastCell.appendChild(button);
        }
    });
    
    
}

function dealwithService(row_data) {
    const rentableDetailsDiv = document.getElementById('RentableDetails');
    rentableDetailsDiv.innerHTML = ""; //cleaning html from the tables of vehicles
    const id = row_data[0];
    const type = row_data[1];
    const reason = "service";
    var today = new Date(); // to find today's date
    const fromdate = new Date(today); // clone the current date
    const todate = new Date(today); // clone the current date
    fromdate.setDate(today.getDate());
    todate.setDate(today.getDate() + 1);
    const replacedby = "none";

    fetch('http://localhost:8080/projectaki/servicerepair', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            id: id,
            type: type,
            reason: reason,
            replacedby: replacedby,
            fromdate: fromdate.toISOString().split('T')[0],
            todate:todate.toISOString().split('T')[0]
                   
            
        })
    })
        .then(response => {
            if (!response.ok) {

                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const responseData = data;
            
        })
        .catch(error => {
        
            console.error('Error:', error);
        });
}



function dealwithRepair(row_data) {
    const rentableDetailsDiv = document.getElementById('RentableDetails');
    rentableDetailsDiv.innerHTML = ""; //cleaning html from the tables of vehicles
    const id = row_data[0];
    const type = row_data[1];
   
    var today = new Date(); // to find today's date
    const fromdate = new Date(today); 
    const todate = new Date(today); 
    fromdate.setDate(today.getDate());
    todate.setDate(today.getDate() + 3);
    const replacedby = "none";

    fetch('http://localhost:8080/projectaki/servicerepair', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            id: id,
            type: type,
            reason: "repair",
            replacedby: replacedby,
            fromdate: fromdate.toISOString().split('T')[0],
            todate:todate.toISOString().split('T')[0]
                   
            
        })
    })
        .then(response => {
            if (!response.ok) {

                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const responseData = data;
            alert(JSON.stringify(responseData));
        })
        .catch(error => {
        
            console.error('Error:', error);
        });
}

function toggleForm() {
        const damagereport = document.getElementsByClassName('form-container-damage')[0];
        damagereport.style.display = 'none';
        const formContainer = document.getElementById("formContainer");
        const toggleFormButton = document.getElementById("toggleFormButton");
        const rentableDetails = document.getElementById("RentableDetails");
        const toggleRentableButton = document.getElementById("toggleRentableButton");
        rentableDetails.style.display = 'none';

        if (formContainer.style.display === "none" || formContainer.style.display === "") {
            formContainer.style.display = "block";
            toggleFormButton.textContent = "Close Form";
            toggleRentableButton.textContent = "Rentable";
        } else {
            formContainer.style.display = "none";
            toggleFormButton.textContent = "Insert Vehicle";
        }
    }
    
async function toggleRentable() {
    const damagereport = document.getElementsByClassName('form-container-damage')[0];
    damagereport.style.display = 'none';
    const toggleFormButton = document.getElementById("toggleFormButton");
    const formContainer = document.getElementById("formContainer");
    const toggleRentableButton = document.getElementById("toggleRentableButton");
    const rentableDetails = document.getElementById("RentableDetails");

    try {
        if (rentableDetails.style.display === "none" || rentableDetails.style.display === "") {
            rentableDetails.style.display = "block";
            await showRentable();
            formContainer.style.display = "none";
            toggleFormButton.textContent = "Insert Vehicle";
            toggleRentableButton.textContent = "Close View";
        } else {
            rentableDetails.style.display = "none";
            toggleRentableButton.textContent = "Rentable";
        }
    } catch (error) {
        alert('An error occurred. Please try again.'); // Provide a user-friendly message
        console.error('Error:', error);
    }
}


function ServiceOrFix(row_data) {
    const modalContainer = document.createElement('div');
    modalContainer.classList.add('modal-container');
    
    const modalContent = document.createElement('div');
    modalContent.classList.add('modal-content');

    const serviceButton = document.createElement('button');
    serviceButton.textContent = 'Service';
    serviceButton.addEventListener('click', function() {
        closeModal();
        dealwithService(row_data);
    });

    const repairButton = document.createElement('button');
    repairButton.textContent = 'Repair';
    repairButton.addEventListener('click', function() {
        closeModal();
        dealwithRepair(row_data); 
    });

    modalContent.appendChild(serviceButton);
    modalContent.appendChild(repairButton);
    modalContainer.appendChild(modalContent);
    document.body.appendChild(modalContainer);
}

function closeModal() {
    const modalContainer = document.querySelector('.modal-container');
    if (modalContainer) {
        modalContainer.remove();
    }
}

function submitCarForm(event) {

    const type = document.querySelector('input[name="vehicle"]:checked').value;
    const brand = document.getElementById('brand').value;
    const model = document.getElementById('model').value;
    const color = document.getElementById('color').value;
    const autonomy = document.getElementById('autonomy').value;
    const registrationNumber = document.getElementById('registration_number').value; 
    const rentable = document.getElementById('rentable').value;
    const rentPrice = document.getElementById('rent_price').value; 
    const insurancePrice = document.getElementById('insurance_price').value; 

    let carType, passengers;

    if (type === "car") {
        carType = document.getElementById('car_type').value;
        passengers = document.getElementById('passengers').value;
    } else {
        carType = "none";
        passengers = 1;
    }

    fetch('http://localhost:8080/projectaki/addandshowvehicle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
         
            type: type,
            brand: brand,
            model: model,
            color: color,
            autonomy: autonomy,
            registrationNumber: registrationNumber,
            carType: carType,
            rentable: rentable,
            passengers: passengers,
            rentPrice: rentPrice,
            insurancePrice: insurancePrice
        }),
    })
        .then(response => {
            if (!response.ok) {

                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const responseData = data;
            alert(JSON.stringify(responseData));
        })
        .catch(error => {
        
            console.error('Error:', error);
        });
}

function toggleForm() {
        const damagereport = document.getElementsByClassName('form-container-damage')[0];
        damagereport.style.display = 'none';
        const formContainer = document.getElementById("formContainer");
        const toggleFormButton = document.getElementById("toggleFormButton");
        const rentableDetails = document.getElementById("RentableDetails");
        const toggleRentableButton = document.getElementById("toggleRentableButton");
        rentableDetails.style.display = 'none';

        if (formContainer.style.display === "none" || formContainer.style.display === "") {
            formContainer.style.display = "block";
            toggleFormButton.textContent = "Close Form";
            toggleRentableButton.textContent = "Rentable";
        } else {
            formContainer.style.display = "none";
            toggleFormButton.textContent = "Insert Vehicle";
        }
    }
    

    
async function showRentable() {
    try {
        const response = await fetch('http://localhost:8080/projectaki/addandshowvehicle', {
            method: 'GET',
            headers: {
                'Content-Type': 'text/plain'
            }
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.text();
        const rentableDetailsDiv = document.getElementById('RentableDetails');
        rentableDetailsDiv.innerHTML = data;
    } catch (error) {
        throw new Error('Error fetching data: ' + error.message);
    }
}