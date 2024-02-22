import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBrain } from "@fortawesome/free-solid-svg-icons";

const NavigationBar = () => {
  return (
    <nav className="navbar navbar-expand-lg bg-primary" data-bs-theme="dark">
      <div style={{ paddingLeft: 50, paddingTop: 10, paddingBottom: 10 }} className="container-fluid">
        <a className="navbar-brand" href="/">
          <FontAwesomeIcon icon={faBrain} style={{ marginRight: "0.5rem"}} />
          IA1
        </a>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarColor01"
          aria-controls="navbarColor01"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon" />
        </button>
      </div>
    </nav>
  );
};

export default NavigationBar;
