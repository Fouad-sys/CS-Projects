using UnityEngine;
public class HittableItem : MonoBehaviour {

    private Rigidbody body;
    private Renderer renderer;

    void Start() {
        body = GetComponent<Rigidbody>();
        renderer = GetComponent<Renderer>();
        renderer.material.color = Color.yellow;
    }

    void OnCollisionEnter(Collision collision)
    {
        // Rigidbody colliderRigidbody = collision.rigidbody;
        // if (collision.gameObject.tag == "Racket" || collision.collider.tag == "Racket")
        //     {
        //         body.velocity = colliderRigidbody.velocity*10f;
        //     }

        var racquet = collision.gameObject.GetComponent<Racket>();
        if (racquet != null)
        {
            //body.velocity = racquet.Velocity * 3f;

             // Calculate the direction from the center of the racket to the point of contact
        Vector3 hitDirection = collision.contacts[0].point - racquet.transform.position;

        // Normalize the direction (so it has a length/magnitude of 1)
        hitDirection = hitDirection.normalized;

        // Reflect the racket's velocity over the hit direction
        Vector3 reflectedVelocity = Vector3.Reflect(racquet.Velocity.normalized, hitDirection);

        // Multiply by the racket's speed
        Vector3 newVelocity = reflectedVelocity * racquet.Velocity.magnitude;



            // You might want to add some extra speed to the ball here, especially if the racket's speed is relatively slow.
            newVelocity *= 2.3f;

            body.velocity = newVelocity;
            renderer.material.color = Color.blue;
        } else {
            renderer.material.color = Color.green;
        }
        // Collider colliderObject = collision.collider;

        // ContactPoint contact = collision.contacts[0];
        // Vector3 contactPointVelocity = body.GetPointVelocity(contact.point);

        // Vector3 direction = colliderObject.transform.position - contact.point;
        // direction.Normalize();
        // float magnitude = Vector3.Dot(contactPointVelocity, direction);

        // float hitForce = magnitude * colliderRigidbody.mass  / body.mass;
        // body.AddVelocitz(direction * hitForce);
        
        // renderer.material.color = Color.green;
        // if(colliderRigidbody == null){
        //     Debug.LogWarning("=============================================\nTheCOlliderObjectIsNULL\n===========================================");
        //     renderer.material.color = Color.black;
        // }
        // if(colliderRigidbody.velocity == null){
        //     Debug.LogWarning("=============================================\nTheColliderVelocityIsNULL\n===========================================");
        //     renderer.material.color = Color.red;
        // }
        
    }
}