console.log("js is working fine");
const baseUrl = "http://localhost:8080";
const viewContactModal = document.getElementById("contact_view_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");
  },
  onShow: () => {
    console.log("modal is shown");
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// instance options object
const instanceOptions = {
  id: "contact_view_modal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function oepnContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

//load contact data

async function loadContactData(id) {
  //load contact data from id
  console.log(id);

  try {
    const data = await (await fetch(`${baseUrl}/api/contacts/${id}`)).json();
    console.log(data);

    document.querySelector("#contact_name").innerHTML = data.name;
    document.querySelector("#contact_email").innerHTML = data.email;
    document.querySelector("#contact_number").innerHTML = data.phoneNumber;
    document.querySelector("#contact_image").src = data.picture;
    document.querySelector("#contact_address").innerHTML = data.address;
    document.querySelector("#contact_description").innerHTML = data.description;

    const contactFavorite = document.querySelector("#contact_favorite");
    if (data.favorite) {
      contactFavorite.innerHTML =
        "<i class='fas fa-star text-yellow-400'></i><i class='fas fa-star text-yellow-400'></i></i><i class='fas fa-star text-yellow-400'></i></i><i class='fas fa-star text-yellow-400'></i></i><i class='fas fa-star text-yellow-400'></i>";
    } else {
      contactFavorite.innerHTML = "Not a favorite contact";
    }
    // document.querySelector("#contact_favorite").innerHTML = data.favorite;
    document.querySelector("#contact_website").innerHTML = data.websiteLink;
    document.querySelector("#contact_website").href = data.websiteLink;
    document.querySelector("#contact_linkedin").innerHTML = data.linkedInLink;
    document.querySelector("#contact_linkedin").href = data.linkedInLink;

    oepnContactModal();
  } catch (error) {
    console.log("Error : ", error);
  }

  // .then(async(response) => {
  //     let data = await response.json()
  //     console.log(data);
  //     return data;
  // }).catch((error=> {
  //     console.log(error);
  // }))
}


//delete contact
async function deleteContact(id) {
  Swal.fire({
    title: "Do you want to delete the contact?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Delete",
  }).then((result) => {
    /* Read more about isConfirmed, isDenied below */
    if (result.isConfirmed) {
      //   Swal.fire("Saved!", "", "success");

      const url = `${baseUrl}/user/contacts/delete/` + id;
      window.location.replace(url);
    }
  });
}
