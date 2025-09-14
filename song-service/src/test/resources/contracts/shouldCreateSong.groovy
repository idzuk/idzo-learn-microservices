package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return created song id"

    request {
        method 'POST'
        url '/songs'
        headers {
            contentType('application/json')
        }
        body(
                name: "Test Title",
                artist: "Test Artist",
                album: "Test Album",
                duration: "03:30",
                id: 1,
                year: "2025"
        )
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(
                id: 1
        )
    }
}
