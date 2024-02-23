import React from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

const Results = () => {
    const location = useLocation();
    const { responseBodyJson } = location.state;
    console.log(responseBodyJson);
    var labels = responseBodyJson['labels'];
    var facesNumber = responseBodyJson['facesNumber'];
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
            <button style={{ marginRight: "300px", width: "75px" }} type="button" className="btn btn-outline-warning" onClick={metodoSubmit}><FontAwesomeIcon icon={faArrowLeft} style={{ marginRight: "0.5rem"}} /></button>
            <h1 style={{ textAlign: "center", display: "flex", justifyContent: "center", alignItems: "center", paddingTop: "5%"}}>{facesNumber}</h1>
        </div>
            <div style={{ paddingLeft: "20%", paddingRight: "20%", textAlign: "center", height: "70vh", width: "200vh", display: "flex", justifyContent: "center", alignItems: "center" }}>
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
