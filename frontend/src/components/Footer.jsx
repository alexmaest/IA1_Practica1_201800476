import React from "react";

const Footer = () => {
  return (
    <div style={{ textAlign: "center", width: "100vh",display: "flex", justifyContent: "center", alignItems: "center", paddingBottom: "1%" }} className="container-fluid">
        <p>Plataforma realizada para la primera práctica del curso de <strong>Inteligencia Artificial 1</strong>, la cuál utiliza el API de Cloud Vision de Google Cloud Servides para la detección de imágenes</p>
    </div>
  );
};

export default Footer;