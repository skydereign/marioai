#! python
#/c/Anaconda/python
import cv2
import json
import numpy as np
import matplotlib.pyplot as plt
import json
				  
def parseLevel(level,tiles):
	level = cv2.imread(level) 

	solidTiles = ['00','10','33','66','99','269','270','271','434','435','436','437','438','439','450'];
	powerupTiles = ['Mushroom','MushroomHidden'];
	breakableTiles = ['01','03','67','68','69'];
	goodTile = ['24','90'];
	enemyTiles = ['turtle1','turtle2','fly1','fly2','fly3','fly4','pirannha','turtle1b','turtle2b','fly1b','fly2b','fly3b','fly4b','pirannha2','goomba','goomba2'];
	bullets = ['09','75'];
	warpPipe = ['pipe_ul']
	coins = ['57','58','123'];
	levelMap = np.zeros((level.shape[0]/16,level.shape[1]/16));


	solidTiles = map(lambda str: 'Tiles/tileset_tile' + str + '.png',solidTiles)
	solidTiles = map( cv2.imread,solidTiles)
	solidLocations = findSubImageLocations(level,solidTiles,0.85)

	powerupTiles = map(lambda str: 'Tiles/tileset_tile' + str + '.png',powerupTiles)
	powerupTiles = map( cv2.imread,powerupTiles)
	puLocations = findSubImageLocations(level,powerupTiles,0.85)	
		
	breakableTiles = map(lambda str: 'Tiles/tileset_tile' + str + '.png',breakableTiles)
	breakableTiles = map( cv2.imread,breakableTiles)
	breakLocations = findSubImageLocations(level,breakableTiles,0.85)	
		
	goodTile = map(lambda str: 'Tiles/tileset_tile' + str + '.png',goodTile)
	goodTile = map( cv2.imread,goodTile)
	goodLocations = findSubImageLocations(level,goodTile,0.85)	

	enemyTiles = map(lambda str: 'Tiles/'+ str + '.png',enemyTiles)
	enemyTiles = map( cv2.imread,enemyTiles)
	enemyLocations = findSubImageLocations(level,enemyTiles,0.4)	

	warpPipe = map(lambda str: 'Tiles/'+ str + '.png',warpPipe)
	warpPipe = map( cv2.imread,warpPipe)
	pipeLocations = findSubImageLocations(level,warpPipe,0.85)

	coins = map(lambda str: 'Tiles/tileset_tile'+ str + '.png',coins)
	coins = map( cv2.imread,coins)
	coinLocations = findSubImageLocations(level,coins,0.8)	

	
	bullets = map(lambda str: 'Tiles/tileset_tile'+ str + '.png',bullets)
	bullets = map( cv2.imread,bullets)
	bulletLocations = findSubImageLocations(level,bullets,0.8)	
	
	plt.imshow(level);


	for ii in range(0,solidLocations[0].size):
		levelMap[clamp(round(solidLocations[0][ii]/16),0,levelMap.shape[0]-1),clamp(round(solidLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 1

	for ii in range(0,breakLocations[0].size):
		levelMap[clamp(round(breakLocations[0][ii]/16),0,levelMap.shape[0]-1),clamp(round(breakLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 2

	for ii in range(0,goodLocations[0].size):
		levelMap[clamp(round(goodLocations[0][ii]/16),0,levelMap.shape[0]-1),clamp(round(goodLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 4

	for ii in range(0,enemyLocations[0].size):
		levelMap[clamp(round(enemyLocations[0][ii]/16),0,levelMap.shape[0]-1),clamp(round(enemyLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 5
		
	for ii in range(0,coinLocations[0].size):
		levelMap[clamp(round(coinLocations[0][ii]/16),0,levelMap.shape[0]-1),clamp(round(coinLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 7

	for ii in range(0,bulletLocations[0].size):
		levelMap[clamp(round(bulletLocations[0][ii]/16),0,levelMap.shape[0]-1),clamp(round(bulletLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 8
		
	for ii in range(0,pipeLocations[0].size):
		for jj in range(0,int(levelMap.shape[0]-1-clamp(round(pipeLocations[0][ii]/16),0,levelMap.shape[0]-1)-1)):
			if (levelMap[clamp(round(pipeLocations[0][ii]/16),0,levelMap.shape[0]-1)+jj,clamp(round(pipeLocations[1][ii]/16),0,levelMap.shape[1]-1)] == 0):
				levelMap[clamp(round(pipeLocations[0][ii]/16),0,levelMap.shape[0]-1)+jj,clamp(round(pipeLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 6
				levelMap[clamp(round(pipeLocations[0][ii]/16),0,levelMap.shape[0]-1)+jj,clamp(round(pipeLocations[1][ii]/16),0,levelMap.shape[1]-1)+1] = 6
	
	for ii in range(0,puLocations[0].size):
		levelMap[clamp(round(puLocations[0][ii]/16),0,levelMap.shape[0]-1)+1,clamp(round(puLocations[1][ii]/16),0,levelMap.shape[1]-1)] = 3
	minTileSize = 2;
	maxTileSize = 8;
	
	#plt.imshow(levelMap)
	#plt.show()
	for sourceSize in range(2,10,2):
		for tileSize in range(2,10,2):
			for ii in range(0,levelMap.shape[1],1):
				#for jj in range(0,levelMap.shape[0],2):
				getTileAndTransitions(tiles,levelMap,ii,0,sourceSize,tileSize,14)
	toRemove = []			
	for tile in tiles:
		if len(tiles[tile]["right"]) == 0:
			toRemove.append(tile)
	while len(toRemove) > 0:
		for tile in toRemove:
			del tiles[tile]
		for tile in tiles:
			for rem in toRemove:
				if rem in tiles[tile]["right"]:
					del tiles[tile]["right"][rem]
		toRemove = []			
		for tile in tiles:
			if len(tiles[tile]["right"]) == 0:
				toRemove.append(tile)
#print tiles
# plt.plot(puLocations[1],puLocations[0],'gx')
# plt.plot(breakLocations[1],breakLocations[0],'bx')
# plt.plot(goodLocations[1],goodLocations[0],'wx')

# plt.plot(coinLocations[1],coinLocations[0],'yo');
# plt.plot(enemyLocations[1],enemyLocations[0],'ro');
# plt.plot(pipeLocations[1],pipeLocations[0],'go');
# plt.show()



def clamp(val,minimum,maximum):
	return max(min(val, maximum), minimum)

def findSubImageLocations(image,subImages,confidence):
	allLocations = [ np.array([]) , np.array([])];
	for subImage in subImages:
	
		result = cv2.matchTemplate(image,subImage,cv2.TM_CCOEFF_NORMED)
		match_indices = np.arange(result.size)[(result>confidence).flatten()]
		locations =  np.unravel_index(match_indices,result.shape)
		allLocations[0] = np.concatenate((allLocations[0],locations[0]))
		allLocations[1] = np.concatenate((allLocations[1],locations[1]))
	return allLocations
	
def prefixPostfix(prefix,str,postfix):
	return prefix + str + postfix

def getTileAndTransitions(tiles,map,xx,yy,tileWidth,width,height):
	tile = getTile(map,xx,yy,tileWidth,height)
	if (tile != ""):
		left = ""
		right = ""
		#up = ""
		#down = ""
		left = getTile(map,xx-width,yy,width,height)
		right = getTile(map,xx+tileWidth,yy,width,height)
		# if (False):#width == 2):
			# left = getTile(map,xx-width*2,yy,width,height)
			# right = getTile(map,xx+width,yy,width,height)
			# up = getTile(map,xx,yy-height*2,width,height)
			# down = getTile(map,xx,yy+height,width,height)
		# else :
			# left = getTile(map,xx+tileWidth-width,yy,width,height)
			# right = getTile(map,xx,yy,width,height)
			# up = getTile(map,xx+tileWidth-width,yy-height+2,width,height)
			# right = getTile(map,xx,yy-height+2,width,height)
			
		if (tile not in tiles):
			#tiles[tile] = {"left":{},"right":{}, "up" : {}, "down" : {}}
			tiles[tile] = {"left":{}, "right":{}}
		
		if (left != ""):
			if (left in tiles[tile]["left"]):
				tiles[tile]["left"][left] += width
			else :
				tiles[tile]["left"][left] = width
		if (right != ""):
			if (right in tiles[tile]["right"]):
				tiles[tile]["right"][right] += width
			else :
				tiles[tile]["right"][right] = width
		# if (down != ""):
			# if (down in tiles[tile]["down"]):
				# tiles[tile]["down"][down] += width
			# else :
				# tiles[tile]["down"][down] = width
		# if (up != ""):
			# if (up in tiles[tile]["up"]):
				# tiles[tile]["up"][up] += width
			# else :
				# tiles[tile]["up"][up] = width

def getTile(map,xx,yy,width,height):
	if (xx + width <= map.shape[1] and yy + height <= map.shape[0] and xx >= 0 and yy >= 0):
		outStr = ""
		for jj in range(yy,yy+height):
			for ii in range(xx,xx+width):
				outStr += str(map[jj,ii]) + ","
			outStr += ";"
		return outStr
	else :
		return "";

levels = [	'Mario1/mario-1-1.png','Mario1/mario-1-2.png','Mario1/mario-1-3.png','Mario1/mario-2-1.png',
			'Mario1/mario-3-1.png','Mario1/mario-3-2.png','Mario1/mario-3-3.png','Mario1/mario-4-1.png',
			'Mario1/mario-4-2.png','Mario1/mario-4-3.png','Mario1/mario-5-1.png','Mario1/mario-5-2.png',
			'Mario1/mario-5-3.png','Mario1/mario-6-1.png','Mario1/mario-6-2.png','Mario1/mario-6-3.png',
			'Mario1/mario-7-1.png','Mario1/mario-8-1.png','Mario1/mario-8-2.png','Mario1/mario-8-3.png']
tiles = {}
for levelFile in levels:
	parseLevel(levelFile,tiles)

json.dump(tiles,open("tiles.json",'w'),sort_keys=True,
                  indent=4, separators=(',', ': '))

