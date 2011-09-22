//var width = $(document).width()-50;
//var height = $(document).height()-50;
ZOOM_CONSTANT = 30;
GRAVITATIONAL_CONSTANT = 600.0;
DECAY_CONSTANT = 15000.0;
PADDING = 20;

function Point(position) {
	this.p = position; // position
	this.v = new Vector(0, 0); // velocity
	this.f = new Vector(0, 0); // force
}
Point.points = [];

Point.prototype.setStuff= function( id, relevance ) {
	this.relevance = relevance;
	this.id = id;

    Point.points.push(this);
};

Point.prototype.applyForce = function(force) {
	this.f = this.f.add(force);
};

// points are slightly repulsed by other points
Point.applyCoulombsLaw = function() {
	var ke = 50.0; // repulsion constant

	Point.points.forEach(function(point1) {
		Point.points.forEach(function(point2) {
			if (point1 !== point2) {
				var d = point1.p.subtract(point2.p);
				var distance = d.magnitude() + 1.0;
				var direction = d.normalise();

				// apply force to each end point
				point1.applyForce(direction.multiply(ke).divide(distance * distance * 0.5));
				point2.applyForce(direction.multiply(ke).divide(distance * distance * -0.5));
			}
		});
	});
};

Point.updateVelocity = function(timestep) {
	var damping = 0.5; // damping constant, points lose velocity over time
	Point.points.forEach(function(p) {
		p.v = p.v.add(p.f.multiply(timestep)).multiply(damping);
		p.f = new Vector(0,0);
	});
};

var POSITIONS = [];
var INDEX = 0;

Point.updatePosition = function(timestep) {
//    var maxD = 0;
//    var meanD = 0;

	Point.points.forEach(function(p) {
//        var sx0 = p.screenX();
//        var sy0 = p.screenY();
		p.p = p.p.add(p.v.multiply(timestep));
        var sx = p.screenX();
        var sy = p.screenY();
        if (sx < PADDING) {
            p.setScreenX(PADDING);
        } else if (sx > width - PADDING) {
            p.setScreenX(width - PADDING);
        }
        if (sy < PADDING) {
            p.setScreenY(PADDING);
        } else if (sy > height - PADDING) {
            p.setScreenY(height - PADDING);
        }
//        var d = Math.sqrt((sx0 - sx) * (sx0 - sx) + (sy0 - sy) * (sy0 - sy));
//        meanD += d;
//        if (d > maxD) {
//            maxD = d;
//        }
	});
//    if (INDEX++ % 100 == 0) {
//        var ps = [];
//        $.each(Point.points, function() {
//            ps.push({x : this.screenX(), y : this.screenY()});
//        });
//        POSITIONS.push(ps);
//    }

//    console.log('meanD: ' + meanD + ', maxD: ' + maxD);
};

Point.printDeltas = function() {
    for (var i = 0; i < POSITIONS.length; i++) {
        var maxD = 0;
        var meanD = 0;

        for (var j = 0; j < Point.points.length; j++) {
            var p0 = POSITIONS[i][j];
            var p1 = Point.points[j];
            var sx0 = p0.x;
            var sy0 = p0.y;
            var sx1 = p1.screenX();
            var sy1 = p1.screenY();
            var d = Math.sqrt((sx0 - sx1) * (sx0 - sx1) + (sy0 - sy1) * (sy0 - sy1));
            meanD += d;
            if (d > maxD) {
                maxD = d;
            }
        }
        console.log('i: ' + i + 'n: ' + Point.points.length +
                ' meanD: ' + meanD / Point.points.length + ', maxD: ' + maxD);
    }
};

// convert point to screen coordinates
Point.prototype.screenX = function() {
	return this.p.x * ZOOM_CONSTANT + width/2.0;
};

// convert point to screen coordinates
Point.prototype.setScreenX = function(x) {
    this.p.x = (x - width/2.0) / ZOOM_CONSTANT;
};

Point.prototype.screenY = function() {
	return this.p.y * ZOOM_CONSTANT + height/2.0;
};


// convert point to screen coordinates
Point.prototype.setScreenY = function(y) {
    this.p.y = (y - height/2.0) / ZOOM_CONSTANT;
};


Magnet.prototype.constructor = Magnet;
function Magnet(position, id) {
	this.position = position;
	this.id = id;
	this.relevances = {};
	
	Magnet.magnets.push(this);
}

Magnet.clear = function() {
    Point.points = [];
    Magnet.magnets  = [];
};


Magnet.magnets = [];

Magnet.findById = function(id) {
    for (var i = 0; i < Magnet.magnets.length; i++) {
        if (Magnet.magnets[i].id == id) {
            return Magnet.magnets[i];
        }
    }
    alert('Error: no magnet found with id ' + id);
};

Magnet.prototype.setPosition = function(x, y) {
    this.position.x = x;
    this.position.y = y;
};

Magnet.prototype.computeDistance = function(pnt) {
	//console.log("value 1: "+Math.sqrt( Math.pow((this.point.p.x - pnt.screenX() ),2) + Math.pow((this.point.p.y - pnt.screenY() ),2) ));
    //console.log("value2: "+this.point.p.subtract(pnt.p).magnitude());
    //return this.point.p.subtract(pnt.p).magnitude();
    return Math.sqrt( Math.pow((this.position.x - pnt.screenX() ),2) + Math.pow((this.position.y - pnt.screenY() ),2) );
};

Magnet.prototype.forceDirection = function(pnt) {
	return (new Vector( (-this.position.x + pnt.screenX()), (-this.position.y + pnt.screenY() ) )).normalise();
};

Magnet.prototype.attractPeople = function() {
	var self = this;
	$.each(Point.points, function(i, p){
		var radius = self.computeDistance(p);

		if( (self.relevances[p.id] == null) || isNaN(self.relevances[p.id]) ) {
            return true; // continue;
		}

        var gForce = self.forceDirection(p).multiply(
			( self.relevances[p.id] * (-1.0) * GRAVITATIONAL_CONSTANT )
		).add(
			self.forceDirection(p).multiply(
				( DECAY_CONSTANT / Math.pow((Math.abs(radius-(60)+10)/30.0),4) )
			)
		);
		p.applyForce( gForce );
	});
};

Magnet.prototype.normalizeRelevances = function() {
	var self = this;
	var sum = 0;
	Point.points.forEach(function(p){
        if ( p.relevance[self.id] != null ) { //TODO contains
            self.relevances[p.id] = p.relevance[self.id];
            sum += p.relevance[self.id];
        }
	});
	$.each(self.relevances, function(key,value) {
		self.relevances[key] = value/sum;
	});
};

function startLayout(threshold) {
	Magnet.magnets.forEach(function(mag){
		mag.normalizeRelevances();
	});
	var count =0;
	while (true) {
		Magnet.magnets.forEach(function(mag){
			mag.attractPeople();
		});
		count++;
		Point.applyCoulombsLaw();
		Point.updateVelocity(0.05);
		Point.updatePosition(0.05);

		// calculate kinetic energy of system
		var k = 0.0;
		Point.points.forEach(function(p){
			var speed = p.v.magnitude();
			k += speed * speed;
		});

		// stop simulation when
		if ( k < threshold || count == 1000) {
            console.log('k = ' + k + ', count = ' + count);
			break;
		}
	}
//    Point.printDeltas();
}
