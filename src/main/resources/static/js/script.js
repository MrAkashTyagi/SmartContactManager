console.log("script loaded");

//change theme work

let currentTheme = getTheme();

//initially->
document.addEventListener("DOMContentLoaded", () => {
  changeTheme();
});

//todo
function changeTheme() {
  //set to web page
  // document.querySelector('html').classList.add(currentTheme);
  chnagePageTheme(currentTheme, currentTheme);

  //set the listner to change theme button
  const changeThemeButton = document.querySelector("#themeChangeButton");

  changeThemeButton.addEventListener("click", (event) => {
    let oldTheme = currentTheme;
    console.log("button clicked");

    if (currentTheme == "dark") {
      //theme ko light krna h
      currentTheme = "light";
    } else {
      //theme ko dark
      currentTheme = "dark";
    }
    console.log(currentTheme);
    chnagePageTheme(currentTheme, oldTheme);
  });
}

//set theme to local storage
function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

//get theme from local storage
function getTheme() {
  let theme = localStorage.getItem("theme");
  return theme ? theme : "light";
}

//chnage current page theme

function chnagePageTheme(theme, oldTheme) {
  //local storage me update krna h
  setTheme(currentTheme);
  //remove the current theme
  if (oldTheme) {
    document.querySelector("html").classList.remove(oldTheme);
  }
  //set the current theme
  document.querySelector("html").classList.add(theme);

  //chnage the text of button
  document
    .querySelector("#themeChangeButton")
    .querySelector("span").textContent = theme == "light" ? "dark" : "light";
}
//end of change page theme

//first request - to server to create order
const paymentStart = () => {
  console.log("payment started...");
  let amount = $("#payment_field").val();
  console.log(amount);
  if (amount == "" || amount == null) {
    // alert("amount is required !!");
    swal("Failed", "Amount is required !!!", "error");
    return;
  }

  //ajax is used to send request to server to create order - jquery

  $.ajax({
    url: "/user/create_order",
    data: JSON.stringify({ amount: amount, info: "order_request" }),
    contentType: "application/json",
    type: "POST",
    dataType: "json",
    success: function (response) {
      if (response.status == "created") {
        //open payment form
        let options = {
          key: "rzp_test_CAn5REuRuX60JT",
          amount: response.amount,
          currency: "INR",
          name: "Smart Contact Manager",
          description: "Donation",
          image:
            "https://learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flcwd_logo.45da3818.png&w=640&q=75",
          order_id: response.id,
          handler: function (response) {
            console.log(response.razorpay_payment_id);
            console.log(response.razorpay_order_id);
            console.log(response.razorpay_signature);
            console.log("payment successful !!");
            // alert("congrates !! payment successful !!");


            updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"paid")

            
          },
          prefill: {
            name: "",
            email: "",
            contact: "",
          },
          notes: {
            address: "New Ankit Vihar",
          },
          theme: {
            color: "#3399cc",
          },
        };

        //initiate the payment

        let rzp = new Razorpay(options);

        rzp.on("payment.failed", function (response) {
          console.log(response.error.code);
          console.log(response.error.description);
          console.log(response.error.source);
          console.log(response.error.step);
          console.log(response.error.reason);
          console.log(response.error.metadata.order_id);
          console.log(response.error.metadata.payment_id);
        //   alert("Oops payment failed !!");
        swal("Failed", "Oops payment failed !!!", "error");
        });

        rzp.open();
      }
      //invoked when success
      console.log(response);
    },
    error: function (error) {
      //invoked when error
      console.log(error);
      alert("something went wrong !!");
    },
  });
};

function updatePaymentOnServer(payment_id,order_id,status){
    $.ajax({
        url: "/user/update_order",
    data: JSON.stringify({ 
            payment_id: payment_id,
            order_id: order_id,
            status:status }),
    contentType: "application/json",
    type: "POST",
    dataType: "json",
    success: function(response){
        swal("Congrates!", "Payment is successfull!", "success");
    },
    error: function(error){
        swal("Failed", "Your payment is successful but we did not get on server, we  will contact you as soon as possible", "error");
    }
    });
}