import React from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

const Results = () => {
    const location = useLocation();
    const { responseBodyJson } = location.state;
    var labels = responseBodyJson['labels'];
    var facesNumber = responseBodyJson['facesNumber'];
    var imageDetectedFaces = responseBodyJson['imageDetectedFaces'];
    const history = useHistory();

    const metodoSubmit = async () => {
        history.push({
            pathname: '/',
            state: { responseBodyJson }
        });
        window.location.reload();
    };

    return (
        <div>
            <div style={{ paddingTop: "5%", paddingLeft: "40%", paddingRight: "20%", textAlign: "center", width: "100vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <button style={{ marginRight: "300px", width: "75px" }} type="button" className="btn btn-outline-warning" onClick={metodoSubmit}><FontAwesomeIcon icon={faArrowLeft} style={{ marginRight: "0.5rem" }} /></button>
                <h1 style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingTop: "5%" }}>Resultados</h1>
            </div>

            <p style={{ textAlign: "center", height: "20vh", display: "flex", justifyContent: "center", alignItems: "center" }} class="lead">Cantidad de rostros detectados: {facesNumber}</p>

            <div style={{ textAlign: "center", height: "40vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <div
                    style={{
                        width: "40vw",
                        height: "50vh",
                        borderRadius: "80px",
                        overflow: "hidden",
                        position: "relative",
                        boxShadow: "0 10px 20px rgba(0, 0, 0, 0.2)"
                    }}
                >
                    <img
                        src={`data:image/jpeg;base64,${imageDetectedFaces}`}
                        alt="Detected Faces"
                        style={{
                            width: "40vw",
                            height: "50vh",
                            backgroundColor: "#d9e3f1",
                            borderRadius: "80px",
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                            fontSize: "22px",
                            color: "#485785",
                            boxShadow: "0 80px 80px rgba(0, 0, 0, 0.1), 0 -4px 80px rgba(255, 255, 255, 0.5)"
                        }}
                    />
                </div>
            </div>

            <p style={{ textAlign: "center", height: "40vh", display: "flex", justifyContent: "center", alignItems: "center" }} class="lead">Etiquetas detectadas</p>

            <div style={{ paddingLeft: "20%", paddingRight: "20%", paddingTop: "5%", paddingBottom: "20%", textAlign: "center", height: "40vh", width: "200vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <table style={{ borderRadius: "10px", overflow: "hidden", textAlign: "center" }} className="table table-hover">
                    <thead>
                        <tr className="table-dark">
                            <th scope="col">Etiqueta</th>
                            <th scope="col">Porcentaje (%)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {labels.map((item, index) => (
                            <tr className="table-type" key={index}>
                                <td>{item.description}</td>
                                <td>{item.score}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Results;
