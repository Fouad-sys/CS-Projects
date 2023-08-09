using UnityEngine;

public class HandController : MonoBehaviour {

	// Store the hand type to know which button should be pressed
	public enum HandType : int { LeftHand, RightHand };
	[Header( "Hand Properties" )]
	public HandType handType;


	// Store the player controller to forward it to the object
	[Header( "Player Controller" )]
	public MainPlayerController playerController;


	// Store all gameobjects containing an Anchor
	// N.B. This list is static as it is the same list for all hands controller
	// thus there is no need to duplicate it for each instance
	static protected ObjectAnchor[] anchors_in_the_scene;

	void Start () {
		// Prevent multiple fetch
		if ( anchors_in_the_scene == null ) anchors_in_the_scene = GameObject.FindObjectsOfType<ObjectAnchor>();
		/*
		if ( drawers_in_the_scene == null ) drawers_in_the_scene = GameObject.FindObjectsOfType<Drawer>();
		character_controller = player.GetComponent<CharacterController>();*/
		Debug.Log( "Started" );
	}


	// This method checks that the hand is closed depending on the hand side
	protected bool is_hand_closed () {
		// Case of a left hand
		if ( handType == HandType.LeftHand ) return
			OVRInput.Get( OVRInput.Button.Three )                           // Check that the A button is pressed
			&& OVRInput.Get( OVRInput.Button.Four )                         // Check that the B button is pressed
			&& OVRInput.Get( OVRInput.Axis1D.PrimaryHandTrigger ) > 0.5     // Check that the middle finger is pressing
			&& OVRInput.Get( OVRInput.Axis1D.PrimaryIndexTrigger ) > 0.5;   // Check that the index finger is pressing


		// Case of a right hand
		else return
			OVRInput.Get( OVRInput.Button.One )                             // Check that the A button is pressed
			&& OVRInput.Get( OVRInput.Button.Two )                          // Check that the B button is pressed
			&& OVRInput.Get( OVRInput.Axis1D.SecondaryHandTrigger ) > 0.5   // Check that the middle finger is pressing
			&& OVRInput.Get( OVRInput.Axis1D.SecondaryIndexTrigger ) > 0.5; // Check that the index finger is pressing
	}

	    protected bool is_magnetic_grabbing_activated () {
        // Case of a left hand
        if ( handType == HandType.LeftHand ) return
            OVRInput.Get( OVRInput.Button.Three )                           // Check that the A button is pressed
            && !OVRInput.Get( OVRInput.Button.Four )                         // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.PrimaryHandTrigger ) > 0.5     // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.PrimaryIndexTrigger ) > 0.5;   // Check that the index finger is pressing

        // Case of a right hand
        else return
            OVRInput.Get( OVRInput.Button.One )                             // Check that the A button is pressed
            && !OVRInput.Get( OVRInput.Button.Two )                          // Check that the B button is pressed
            && OVRInput.Get( OVRInput.Axis1D.SecondaryHandTrigger ) > 0.5   // Check that the middle finger is pressing
            && OVRInput.Get( OVRInput.Axis1D.SecondaryIndexTrigger ) > 0.5; // Check that the index finger is pressing
    }

	


	// Automatically called at each frame
	void Update () { handle_controller_behavior(); }


	// Store the previous state of triggers to detect edges
	protected bool is_hand_closed_previous_frame = false;

	// Store the object atached to this hand
	// N.B. This can be extended by using a list to attach several objects at the same time
	protected ObjectAnchor object_grasped = null;




	/// <summary>
	/// This method handles the linking of object anchors to this hand controller
	/// </summary>
	protected void handle_controller_behavior () {

		// Check if there is a change in the grasping state (i.e. an edge) otherwise do nothing
		bool hand_closed = is_hand_closed();

		if ( hand_closed == is_hand_closed_previous_frame ) return;
		is_hand_closed_previous_frame = hand_closed;
		Debug.LogWarningFormat( "Passing" );

		//==============================================//
		// Define the behavior when the hand get closed //
		//==============================================//
		
		if ( hand_closed ) {

			Debug.LogWarningFormat( "Hand closed" );
			// Log hand action detection
			Debug.LogWarningFormat( "{0} get closed", this.transform.parent.name );


			// Determine which object available is the closest from the left hand
			int best_object_id = -1;
			float best_object_distance = float.MaxValue;
			float object_distance;

			// Iterate over objects to determine if we can interact with it
			for ( int i = 0; i < anchors_in_the_scene.Length; i++ ) {

				// Skip object not available
				if ( !anchors_in_the_scene[i].is_available() ) continue;

				// Compute the distance to the object
				object_distance = Vector3.Distance( this.transform.position, anchors_in_the_scene[i].transform.position );

				// Keep in memory the closest object
				// N.B. We can extend this selection using priorities
				if ( object_distance < best_object_distance && object_distance <= anchors_in_the_scene[i].get_grasping_radius() ) {
					best_object_id = i;
					best_object_distance = object_distance;
				}
			}

			// If the best object is in range grab it
			if ( best_object_id != -1 ) {

				// Store in memory the object grasped
				object_grasped = anchors_in_the_scene[best_object_id];

				// Log the grasp
				Debug.LogWarningFormat( "{0} grasped {1}", this.transform.parent.name, object_grasped.name );

				// Grab this object
				object_grasped.attach_to( this );

				if (object_grasped.rigidBody()) {
					Rigidbody rb = object_grasped.rigidBody();
					rb.isKinematic = true;
					rb.useGravity = false;
				}

			}

		//==============================================//
		// Define the behavior when the hand get opened //
		//==============================================//
		} else if ( object_grasped != null) {
			// Log the release
			Debug.LogWarningFormat("{0} released {1}", this.transform.parent.name, object_grasped.name );

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

			// Release the object
			object_grasped.detach_from( this );
		}
	}

}