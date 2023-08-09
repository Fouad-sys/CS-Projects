using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Attract : HandController
{

    [Header( "Maximum Distance" )]
    [Range( 2f, 30f )]
    // Store the maximum distance the player can teleport
    public float maximumTeleportationDistance = 15f;

    private bool teleportation_locked = false;
    private bool teleportation_active = false;
    private Vector3 teleportation_position;
    private bool lerped = false;

    protected CharacterController character_controller;
    [Header( "Marker" )]
    // Store the refence to the marker prefab used to highlight the targeted point
    public GameObject markerPrefab;
    private bool magnet_grab = false;
    public GameObject player;
    bool can_release = false;

    void Start () {
        // Prevent multiple fetch
        if ( anchors_in_the_scene == null ) anchors_in_the_scene = GameObject.FindObjectsOfType<ObjectAnchor>();
        /*
        if ( drawers_in_the_scene == null ) drawers_in_the_scene = GameObject.FindObjectsOfType<Drawer>();*/
        character_controller = player.GetComponent<CharacterController>();
        Debug.Log( "Started" );
        Debug.LogWarningFormat( "Updated" );
    }

    // This method checks that magnetic grab mode is activated depending on the hand side
    protected bool is_magnet_activated () {
        // Case of a left hand
        if ( handType == HandType.LeftHand ) return
            !OVRInput.Get( OVRInput.Button.Three )                           // Check that the A button is pressed
            && !OVRInput.Get( OVRInput.Button.Four )                         // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.PrimaryHandTrigger ) > 0.5     // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.PrimaryIndexTrigger ) > 0.5;   // Check that the index finger is pressing
        else return !OVRInput.Get( OVRInput.Button.One )                             // Check that the A button is pressed
            && !OVRInput.Get( OVRInput.Button.Two )                          // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.SecondaryHandTrigger ) > 0.5   // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.SecondaryIndexTrigger ) > 0.5; // Check that the index finger is pressing

    }

    protected bool is_teleportation_mode_activated () {
        // Case of a left hand
        if ( handType == HandType.LeftHand ) return
            !OVRInput.Get( OVRInput.Button.Three )                           // Check that the A button is pressed
            && OVRInput.Get( OVRInput.Button.Four )                         // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.PrimaryHandTrigger ) < 0.5     // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.PrimaryIndexTrigger ) < 0.5;   // Check that the index finger is pressing

        // Case of a right hand
        else return
            !OVRInput.Get( OVRInput.Button.One )                             // Check that the A button is pressed
            && OVRInput.Get( OVRInput.Button.Two )                          // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.SecondaryHandTrigger ) < 0.5   // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.SecondaryIndexTrigger ) < 0.5; // Check that the index finger is pressing
    }

    protected bool is_teleportation_activated () {
        // Case of a left hand
        if ( handType == HandType.LeftHand ) return
            !OVRInput.Get( OVRInput.Button.Three )                           // Check that the A button is pressed
            && OVRInput.Get( OVRInput.Button.Four )                         // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.PrimaryHandTrigger ) > 0.5     // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.PrimaryIndexTrigger ) < 0.5;   // Check that the index finger is pressing
        // Case of a right hand
        else return
            !OVRInput.Get( OVRInput.Button.One )                             // Check that the A button is pressed
            && OVRInput.Get( OVRInput.Button.Two )                          // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.SecondaryHandTrigger ) > 0.5   // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.SecondaryIndexTrigger ) < 0.5; // Check that the index finger is pressing
    }

    protected bool is_teleportation_deactivated () {
        // Case of a left hand
        if ( handType == HandType.LeftHand ) return
            !OVRInput.Get( OVRInput.Button.Four );                         // Check that the B button is not pressed

        // Case of a right hand
        else return
            !OVRInput.Get( OVRInput.Button.Two );                          // Check that the B button is not pressed
    }


    // Automatically called at each frame
    void Update () { handle_attraction_behavior(); }

    protected GameObject marker_prefab_instanciated;

    protected bool magnet_activated = false;

    protected Magnetic grasped_magnetic = null;


    /// <summary>
    /// This method handles the linking of object anchors to this hand controller
    /// </summary>
    protected void handle_attraction_behavior () {

        if (is_teleportation_mode_activated()) {
            Debug.LogWarningFormat( "Teleportation mode activated" );
            handle_teleportation_mode();
        } 

        if (teleportation_active && is_teleportation_activated() && teleportation_position != null) {
            character_controller.Move(teleportation_position - this.transform.position);
            //teleportation_locked = true;
            teleportation_active = false;
        }

        teleportation_locked = false;
        bool magnet_mode = is_magnet_activated();

        if (object_grasped != null && !is_magnetic_grabbing_activated() && !is_hand_closed() && can_release) {
            Debug.LogWarningFormat("{0} trying to be released", grasped_magnetic.name );
            if (object_grasped.rigidBody() != null) {
				Rigidbody rb = object_grasped.rigidBody();
				rb.isKinematic = false;
				rb.useGravity = true;
				if (handType == HandType.LeftHand) {
					rb.velocity = OVRInput.GetLocalControllerVelocity(OVRInput.Controller.LTouch);
				} else {
					rb.velocity = OVRInput.GetLocalControllerVelocity(OVRInput.Controller.RTouch);
				}
			}
            object_grasped.detach_from(this);
            object_grasped = null;
            can_release = false;
        }

           

        if (magnet_mode) {
            handle_magnet_mode();
            Debug.LogWarningFormat( "In magnet mode" );
            // Log hand action detection
        }

        if (is_magnetic_grabbing_activated() && magnet_grab) {
            Debug.LogWarningFormat( "Dragged magnetic avtivateddd" );
            grasped_magnetic.drag(this.transform.position);
            magnet_grab = false;
        }

        if (magnet_activated) {
            Debug.LogWarningFormat( "Magnt activatd" );
            float magnet_distance = Vector3.Distance( this.transform.position, grasped_magnetic.transform.position );
            if (object_grasped.rigidBody()) {
                    Rigidbody rbm = grasped_magnetic.rigidBody();
                    rbm.isKinematic = true;
                    rbm.useGravity = false;
            }
            if (magnet_distance <= object_grasped.get_grasping_radius()) {
                object_grasped.attach_to(this);
                can_release = true;
                magnet_activated = false;
                destroy_marker();
            }
        }   
        
        if (is_teleportation_deactivated()) {
            teleportation_active = false;
        }
        
        if (!magnet_mode && !is_teleportation_mode_activated()) {
            destroy_marker();
        }
    }


    protected void handle_teleportation_mode() {
        Vector3 target_point = new Vector3();

        RaycastHit hit;

        if (Physics.Raycast(this.transform.position, this.transform.forward, out hit, Mathf.Infinity)) {
            target_point = hit.point;
            if (hit.distance <= maximumTeleportationDistance) {
                Debug.LogWarningFormat( "Ready to teleport" );
                if (marker_prefab_instanciated == null) {
                    marker_prefab_instanciated = GameObject.Instantiate( markerPrefab, this.transform);
                    Debug.LogWarningFormat( "Creating marker" );
                }
                Debug.LogWarningFormat( "Marker present" );
                marker_prefab_instanciated.transform.position = target_point;
                teleportation_active = true;
                teleportation_position = hit.point;
            }

        } else {
            destroy_marker();
            //teleportation_active = false;
        }
    }

    protected void handle_magnet_mode() {
        Vector3 target_point = new Vector3();

        RaycastHit hit;
                                      
        if (Physics.Raycast(this.transform.position, this.transform.forward, out hit, Mathf.Infinity)) {
            target_point = hit.point;
            
            if (marker_prefab_instanciated == null) {
                marker_prefab_instanciated = GameObject.Instantiate( markerPrefab, this.transform);
                Debug.LogWarningFormat( "Creating marker" );
            }
            marker_prefab_instanciated.transform.position = target_point;

            Magnetic magnetic_object = hit.transform.GetComponent<Magnetic>();
            if (magnetic_object != null) {
                Debug.LogWarningFormat( "Got magnetic component" );
                magnet_activated = true;
                magnet_grab = true;
                grasped_magnetic = magnetic_object;
                object_grasped = grasped_magnetic;
            }

        } else {
            destroy_marker();
        }
    }

    protected void destroy_marker() {
        if (marker_prefab_instanciated != null) Destroy(marker_prefab_instanciated);
        marker_prefab_instanciated = null;
    }
}
